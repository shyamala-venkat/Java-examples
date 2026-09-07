/**
 * CONCEPT: Class vs Object — blueprints and instances
 *
 * A class is a compile-time blueprint. An object is a runtime instance.
 * Understanding the difference between instance state and behavior
 * is the first step toward good OOP design.
 *
 * Why this matters in interviews:
 *   - Every design question starts here: what are the objects?
 *   - Instance variables vs class (static) variables — a common confusion
 *   - Object identity (==) vs equality (.equals()) is a constant interview topic
 */
class ClassAndObject {

    // --- A well-designed class: state is private, behavior is public ---
    static class Order {
        private final String orderId;
        private String status;
        private double total;

        Order(String orderId, double total) {
            this.orderId = orderId;
            this.total   = total;
            this.status  = "PENDING";
        }

        void confirm()  { this.status = "CONFIRMED"; }
        void ship()     { this.status = "SHIPPED"; }
        void deliver()  { this.status = "DELIVERED"; }

        boolean isComplete() { return "DELIVERED".equals(status); }

        @Override
        public String toString() {
            return "[" + orderId + "] $" + total + " — " + status;
        }
    }

    public static void main(String[] args) {

        // Creating objects: each `new` call allocates a separate object on the heap
        Order o1 = new Order("ORD-001", 149.99);
        Order o2 = new Order("ORD-002", 49.00);

        System.out.println("Before: " + o1);
        o1.confirm();
        o1.ship();
        System.out.println("After:  " + o1);
        System.out.println("o2 unchanged: " + o2); // modifying o1 never affects o2

        // --- Identity vs equality ---
        Order a = new Order("X", 10);
        Order b = new Order("X", 10);
        Order c = a;                     // c is another reference to the SAME object as a

        System.out.println("\nIdentity (==):");
        System.out.println("a == b: " + (a == b)); // false — different objects
        System.out.println("a == c: " + (a == c)); // true  — same object

        // Mutating through one reference affects all references to the same object
        c.confirm();
        System.out.println("a after c.confirm(): " + a); // a shows CONFIRMED

        // --- Object state drives method behavior ---
        System.out.println("\nOrder lifecycle:");
        Order shipment = new Order("SHIP-100", 299.99);
        System.out.println(shipment.isComplete()); // false
        shipment.confirm();
        shipment.ship();
        shipment.deliver();
        System.out.println(shipment.isComplete()); // true

        // TRY THIS:
        // 1. Add a cancel() method that only works if status is PENDING or CONFIRMED.
        //    Trying to cancel a SHIPPED order should print an error. Test it.
        // 2. Add an `applyDiscount(double percent)` method — validate that percent is 0-100.
        // 3. Override equals() in Order so two Orders with the same orderId are "equal".
        //    Test: new Order("X", 10).equals(new Order("X", 20)) should be true.
    }
}
