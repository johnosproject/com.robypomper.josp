package com.robypomper.josp.jod.structure;

import java.util.Collection;

public interface JODContainer extends JODComponent {
    Collection<JODComponent> getComponents();
}
