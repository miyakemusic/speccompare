package positioningmap;

public class ProductName {
	private String vendorName;
	private String familyName;
	private String productName;
	
	public ProductName(String string) {
		this.text(string);
	}

	public void text(String string) {
		String[] tmp = string.split("\n");
		if (tmp.length == 1) {
			this.vendorName = tmp[0];
		}
		else if (tmp.length == 2) {
			this.vendorName = tmp[1];
		}
		else if (tmp.length == 3) {
			this.productName = tmp[2];
		}
	}

	public String getVendorName() {
		return vendorName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public String getProductName() {
		return productName;
	}
	
	
}
