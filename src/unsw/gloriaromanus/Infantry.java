package unsw.gloriaromanus;

/**
 * Infantry unit extends Battalion
 */
public class Infantry extends Battalion {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an infantry unit.
     *
     * @param name String
     * @param type String
     * @param sale int
     */
    public Infantry(String name, String type, int sale) {
        super(name, type, sale);
        super.setEngagement("Melee");
    }

}
