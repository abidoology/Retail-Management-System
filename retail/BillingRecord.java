package retail;

public class BillingRecord {
    private int billId;
    private int customerId;
    private String productName;
    private int quantity;
    private double price;
    private double total;
    private String createdAt;

    public BillingRecord(int billId, int customerId, String productName, int quantity, double price, double total, String createdAt) {
        this.billId = billId;
        this.customerId = customerId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.createdAt = createdAt;
    }

    // Getters 
    public int getBillId() { return billId; }
    public int getCustomerId() { return customerId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getTotal() { return total; }
    public String getCreatedAt() { return createdAt; }
}

