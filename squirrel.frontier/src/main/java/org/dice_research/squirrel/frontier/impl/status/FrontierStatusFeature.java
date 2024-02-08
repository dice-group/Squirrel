package org.dice_research.squirrel.frontier.impl.status;

import java.util.function.Supplier;

public class FrontierStatusFeature {

    public String name;
    public Supplier<String> valueSupplier;

    public FrontierStatusFeature(String name, Supplier<String> valueSupplier) {
        super();
        this.name = name;
        this.valueSupplier = valueSupplier;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the valueSupplier
     */
    public Supplier<String> getValueSupplier() {
        return valueSupplier;
    }

    /**
     * @param valueSupplier the valueSupplier to set
     */
    public void setValueSupplier(Supplier<String> valueSupplier) {
        this.valueSupplier = valueSupplier;
    }

}
