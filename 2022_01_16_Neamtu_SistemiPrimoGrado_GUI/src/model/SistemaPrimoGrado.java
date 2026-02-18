package model;

public class SistemaPrimoGrado {
    //attributi
    private EqPrimoGrado eq1;
    private EqPrimoGrado eq2;


    //costruttore
    public SistemaPrimoGrado(EqPrimoGrado eq1, EqPrimoGrado eq2) {
        this.eq1=eq1;
        this.eq2=eq2;
    }


    //metodi get
    public EqPrimoGrado getEq1() {
        return eq1;
    }

    public EqPrimoGrado getEq2() {
        return eq2;
    }


    //altri metodi
    public String risolvi() {
        String app="";
        double d = (eq1.getA()*eq2.getB())-(eq2.getA()*eq1.getB()); //(a1*b2)-(a2*b1)=0

        if (d==0) {
            throw new IndetImpException();
        }

        double dx = (eq1.getC()*eq2.getB())-(eq2.getC()*eq1.getB()); //(c1*b2)-(c2-b1)
        double dy = (eq1.getA()*eq2.getC())-(eq2.getA()*eq1.getC()); //(a1*c2)-(a2*c1)
        double x = dx/d;
        double y = dy/d;

        app="Risultati:\nx=" + x+ "\ny=" + y + "\n";
        return app;
    }


    //toString
    public String toString() {
        String app;
        app="( "+eq1.getA()+"X+("+eq1.getB()+")Y="+eq1.getC()+"\n";
        app+="( "+eq2.getA()+"X+("+eq2.getB()+")Y="+eq2.getC()+"\n";
        return app;
    }
}