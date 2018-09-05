package com.lostagain.Jam.Movements;

import com.google.common.primitives.Doubles;

//ok, this class is too complex to explain, sorry
public class SimpleVector3 {
	
	public double x;
	public double y;
	public double z;
	

	public SimpleVector3(double x, double y,double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	
@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		SimpleVector3 other = (SimpleVector3) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}


/**
 * splits a comma separated string into x,y,z doubles
 * if no comma is found, we assume they should all be set to the same value
 * 
 * @param parameterOne
 */
	public SimpleVector3(String params) {
		
		String[] values = params.split(",");
		if (values.length>1){
			x = Double.parseDouble(values[0]);
			y = Double.parseDouble(values[1]);
			z = Double.parseDouble(values[2]);
		} else {
			x = Double.parseDouble(values[0]);
			y = Double.parseDouble(values[0]);
			z = Double.parseDouble(values[0]);
				
		}
		
	}




	@Override
	public String toString() {
		return x+","+y+","+z;
	}

	

	/**
	 * add to this vector
	 * @param val
	 * @return
	 */
	public SimpleVector3 add(SimpleVector3 val) {
		x =	x + val.x;
		y=y + val.y;
		z=z + val.z;
		return this;
		
	}


	/**
	 * sub from this vector
	 * @param val
	 * @return
	 */
	public SimpleVector3 sub(SimpleVector3 val) {
		x =	x - val.x;
		y=y - val.y;
		z=z - val.z;
		return this;
		
	}

	public SimpleVector3 copy() {
		return new SimpleVector3(x,y,z);
	}


	
	public double dot(SimpleVector3 withThis) {
		 return (x * withThis.x) + (y * withThis.y) + (z * withThis.z);
	}


	/**
	 * scales this vectors x,y,z by the amount specified
	 * @param d
	 * @return
	 */
	public SimpleVector3 mul(double d) {
		x=x*d;
		y=y*d;
		z=z*d;
		
		return this;
	}


	/**
	 * converts this to a unit vector
	 */
	public void normalize() {
		//get size
		double length=getLength();
		//normalize
		x=x/length;
		y=y/length;
		z=z/length;
		
		
	}

	

	public double getLength() {
		 return Math.sqrt((x * x) + (y * y) + (z * z));

	}


	public void set(double nx, double ny, double nz) {
		
		x=nx;
		y=ny;
		z=nz;	
		
	}


	public void set(SimpleVector3 newvalues) {

		x=newvalues.x;
		y=newvalues.y;
		z=newvalues.z;
	}

	
/** changes this object by multiplying with scale, then returns itself **/
	
	public SimpleVector3 mul(SimpleVector3 scale) {
		
		x=x*scale.x;
		y=y*scale.y;
		z=z*scale.z;
		
		return this;
	}
	
/** changes this object by dividing by scale, then returns itself **/
	
	public SimpleVector3 div(SimpleVector3 scale) {
		
		
		
		x=x/scale.x;
		y=y/scale.y;
		z=z/scale.z;
		
		return this;
	}

}