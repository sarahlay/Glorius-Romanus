package unsw.gloriaromanus;

/**
 * Artillery Unit extends Battalion.
 */
public class Artillery extends Battalion {

    private static final long serialVersionUID = -1418710084550576491L;

    public Artillery(String name, String type, int sale) {
        super(name, type, sale);
        super.setEngagement("Ranged");
    }

}
