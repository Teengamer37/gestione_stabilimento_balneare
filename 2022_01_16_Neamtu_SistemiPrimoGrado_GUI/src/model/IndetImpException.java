package model;

public class IndetImpException extends RuntimeException {
	public IndetImpException(String msg) {
		super(msg);
	}
	
	public IndetImpException() {
		super("Equazione indeterminata o impossibile");
	}
}