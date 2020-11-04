package com.robypomper.communication;

import com.robypomper.communication.client.DefaultClient;
import com.robypomper.communication.peer.PeerInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class CommunicationBase {

    public static final String DELIMITER_STR = "##-$$";
    public static final byte[] DELIMITER = DELIMITER_STR.getBytes(PeerInfo.CHARSET);

    public static int indexOf(byte[] array, byte[] target) {
        if (array.length < target.length)
            return -1;

        for (int i = 0; i < array.length - target.length + 1; i++)
            if (array[i] == target[0]) {
                boolean contains = true;
                for (int k = 0; k < target.length; k++)
                    contains &= array[i + k] == target[k];
                if (contains)
                    return i;
            }
        return -1;
    }

    public static boolean contains(byte[] array, byte[] target) {
        return indexOf(array, target) >= 0;
    }

    public static byte[] beforeDelimiter(byte[] array, byte[] delimiter) {
        int index = indexOf(array, delimiter);
        return Arrays.copyOf(array, index);
    }

    public static byte[] afterDelimiter(byte[] array, byte[] delimiter) {
        int index = indexOf(array, delimiter);
        if (index == -1)
            return new byte[0];
        byte[] b = Arrays.copyOfRange(array, index + delimiter.length, array.length);
        return b;
    }

    public static byte[] append(byte[] destArray, byte[] srcArray) {
        byte[] dataTmp = new byte[destArray.length + srcArray.length];
        System.arraycopy(destArray, 0, dataTmp, 0, destArray.length);
        System.arraycopy(srcArray, 0, dataTmp, destArray.length, srcArray.length);
        return dataTmp;
    }

    public static String truncateMid(byte[] array, int count) {
        return truncateMid(new String(array, PeerInfo.CHARSET), count);
    }

    public static String truncateMid(String str, int count) {
        if (str.length() < count)
            return str;
        int truncSize = (count / 2) - 2;
        String endStr = str.substring(str.length() - truncSize);
        if (endStr.trim().isEmpty())
            endStr = "-EMPTY END-";
        return str.substring(0, truncSize) + " .. " + endStr;
    }

    public static byte[] trim(byte[] array) {
        int from = 0;
        int to = array.length;
        while (from < array.length && array[from] == ' ')
            from++;
        while (to > 0 && array[to - 1] == ' ')
            to--;

        return Arrays.copyOfRange(array, from, to);
    }

    public static byte[][] listenForData(DataInputStream in, byte[] dataBuffered, boolean delimiter) throws IOException {
        byte[] dataRead = new byte[0];
        int bytesRead = 0;

        // Get buffered data
        if (dataBuffered.length > 0) {
            dataRead = dataBuffered;
            bytesRead = dataBuffered.length;
            dataBuffered = new byte[0];
        }

        while (bytesRead != -1 && !(delimiter && DefaultClient.contains(dataRead, DELIMITER))) {
            int available = in.available();
            byte[] dataRead_More = new byte[available > 0 ? available : 1];
            bytesRead = in.read(dataRead_More);
            dataRead_More = DefaultClient.trim(dataRead_More);
            dataRead = DefaultClient.append(dataRead, dataRead_More);
        }

        // Check if disconnected by client
        if (bytesRead == -1)
            return null;

        if (delimiter && DefaultClient.contains(dataRead, DELIMITER)) {
            dataBuffered = DefaultClient.afterDelimiter(dataRead, DELIMITER);
            dataRead = DefaultClient.beforeDelimiter(dataRead, DELIMITER);
        }

        byte[][] ret = new byte[2][];
        ret[0] = dataRead;
        ret[1] = dataBuffered;
        return ret;
    }

    public static void transmitData(DataOutputStream out, byte[] data, boolean delimiter) throws IOException {
        synchronized (out) {
            if (delimiter)
                data = DefaultClient.append(data, DefaultClient.DELIMITER);
            out.write(data, 0, data.length);
        }
    }

}
