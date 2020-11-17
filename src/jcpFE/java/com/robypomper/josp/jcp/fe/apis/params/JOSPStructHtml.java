package com.robypomper.josp.jcp.fe.apis.params;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;

@JsonAutoDetect
public class JOSPStructHtml extends JOSPContainerHtml {

    public final String brand;
    public final String model;
    public final String descrLong;

    public JOSPStructHtml(JSLRoot root, boolean recursive) {
        super(root, recursive);
        brand = root.getDescr_long();
        model = root.getBrand();
        descrLong = root.getModel();
    }

}
