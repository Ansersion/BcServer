package db;

public class DeviceInfoUnit {
	private DevInfoHbn devInfoHbn;
	private SnInfoHbn snInfoHbn;

	public DevInfoHbn getDevInfoHbn() {
		return devInfoHbn;
	}
 
	public void setDevInfoHbn(DevInfoHbn devInfoHbn) {
		this.devInfoHbn = devInfoHbn;
	}

	public SnInfoHbn getSnInfoHbn() {
		return snInfoHbn;
	}

	public void setSnInfoHbn(SnInfoHbn snInfoHbn) {
		this.snInfoHbn = snInfoHbn;
	}

	@Override
	public String toString() {
		return "DeviceInfoUnit [devInfoHbn=" + devInfoHbn + "]";
	}
}
