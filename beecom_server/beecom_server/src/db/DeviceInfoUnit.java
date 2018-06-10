package db;

public class DeviceInfoUnit {
	private DevInfoHbn devInfoHbn;
	
	public DeviceInfoUnit() {
		this.devInfoHbn = null;
	}

	public DeviceInfoUnit(DevInfoHbn devInfoHbn) {
		super();
		this.devInfoHbn = devInfoHbn;
	}

	public DevInfoHbn getDevInfoHbn() {
		return devInfoHbn;
	}

	public void setDevInfoHbn(DevInfoHbn devInfoHbn) {
		this.devInfoHbn = devInfoHbn;
	}

	@Override
	public String toString() {
		return "DeviceInfoUnit [devInfoHbn=" + devInfoHbn + "]";
	}
}
