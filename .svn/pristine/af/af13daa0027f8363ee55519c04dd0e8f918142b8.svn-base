package com.lostagain.Jam.SceneObjects;

public class linktype {
	
	boolean linkX = true;
	boolean linkY = true;
	boolean linkZ = true;
	//in future have rotations too?
	
	public static linktype allAxis = new linktype(true,true,true); //default static link
	
	public linktype(boolean linkX, boolean linkY, boolean linkZ) {
		super();
		this.linkX = linkX;
		this.linkY = linkY;
		this.linkZ = linkZ;
	}

	/**
	 * x,y,z booleans comma separated
	 * (eg, true,true,false for linking in xy but not z)
	 * @param value
	 */
	public linktype(String value) {
		
		String[] bits = value.split(",");
		
		linkX = Boolean.parseBoolean(bits[0]);
		linkY = Boolean.parseBoolean(bits[1]);
		linkZ = Boolean.parseBoolean(bits[2]);
		
		
		
	}

	public boolean isXlinked() {
		return linkX;
	}
	public boolean isYlinked() {
		return linkY;
	}
	public boolean isZlinked() {
		return linkZ;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (linkX ? 1231 : 1237);
		result = prime * result + (linkY ? 1231 : 1237);
		result = prime * result + (linkZ ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		linktype other = (linktype) obj;
		
		if (linkX != other.linkX)
			return false;
		if (linkY != other.linkY)
			return false;
		if (linkZ != other.linkZ)
			return false;
		return true;
	}
	

}
