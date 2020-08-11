package com.robypomper.josp.jcpfe.apis.params;

import com.robypomper.josp.jcpfe.apis.StructureController;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;

import java.util.List;

public class JOSPContainerHtml extends JOSPComponentHtml {

    public final List<JOSPComponentHtml> subComps;

    public JOSPContainerHtml(JSLContainer container, boolean recursive) {
        super(container);
        subComps = recursive ? StructureController.convert(container.getComponents()) : null;
    }

}
