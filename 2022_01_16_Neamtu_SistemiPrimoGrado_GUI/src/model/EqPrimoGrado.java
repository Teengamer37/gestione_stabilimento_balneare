package model;

public class EqPrimoGrado {
	//attributi
	private double a;
	private double b;
	private double c;


	//costruttore
	public EqPrimoGrado(double a, double b, double c) {
		this.a=a;	
		this.b=b;
		this.c=c;
	}


	//metodi get
	public double getA() {
		return a;
	}

	public double getB() {
		return b;
	}

	public double getC() {
		return c;
	}


	//toString
	public String toString() {
		return "\n" + a + " x (" + b + ") = "; 
	}
}