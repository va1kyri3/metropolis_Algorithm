import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class MetropolisThread extends Thread {
	
	//Variable declarations
	private int n = 0;
	private double B = 0;
	private double C = 0;
	private double T = 0;
	private int Nm = 0;
	private int Nf = 0;
	public double mTot = 0;
	public double CpTot = 0;	
	
	//Thread constructor
	MetropolisThread(int qSpins, double Bval, double Cval, double Tval, int numFlips, int numCalls){
		this.n = qSpins;
		this.B = Bval;
		this.C = Cval;
		this.T = Tval;
		this.Nf = numFlips;
		this.Nm = numCalls;
	}
	
	//metropolis algorithm
	public static double[] metropolis(int n, double B, double C, double T, int Nf) {
		double Energy = 0;
		int parity = -1;
		double m = 0;
		double Cp = 0;
		double p=0, r=0;
		int randInt;	
		int next = 0;		
		
		//Initialize quantum spin linked lists
		LinkedList<Integer> qS0 = new LinkedList<Integer>();
		LinkedList<Integer> qS1 = new LinkedList<Integer>();
		double [] results = new double [2];		
		
		//Initial spin configuration
		if(C>=0) {
			for(int i = 0; i < n; i++) {
				qS0.add(1);
				qS1.add(1);			
			}
		}
		if(C<0) {
			for(int i = 0; i < n; i++) {
				parity *= -1;
				qS0.add(1*parity);
				qS1.add(1*parity);
			}
		}
		
		//Flip the spins
		for(int iter = 0; iter < Nf; iter++) {
			Energy = 0;
			randInt = ThreadLocalRandom.current().nextInt(n);
			if(qS0.get(randInt) == 1){
				qS1.set(randInt, -1);
			}
			if(qS0.get(randInt) == -1) {
				qS1.set(randInt, 1);
			}				
	
			//Calculate total energy difference between spins
			for(int i = 0; i < n; i++) {
		
				//Set linked list to circular
				if(i == n-1) {
					next = 0;
				}else {
				next = i+1;
				}				
				
				//Calculate energy difference of spin configurations
				Energy += (((B*qS0.get(i)) + (C*(qS0.get(i)*qS0.get(next)))) - ((B*qS1.get(i)) + (C*(qS1.get(i)*qS1.get(next)))));
		
			}
	
			//Determine whether or not to replace S0
			if(Energy < 0) {
				for(int i = 0; i < n; i++) {
					qS0.set(i, qS1.get(i));
				}
			}
			if(Energy > 0) {
				p = Math.exp(-Energy/T);
				r = ThreadLocalRandom.current().nextDouble(1);
		
				if(r<p) {
					for(int i = 0; i < n; i++) {
						qS0.set(i, qS1.get(i));
					}
				}else {
				for(int i = 0; i < n; i++) {
					qS1.set(i, qS0.get(i));
					}
				}				
			}
		}
		//Computing magnetization per spin and pair correlation
		for(int i = 0; i < n; i++) {
			m += qS0.get(i);
		}
		
		results[0] = m/n;
		
		for(int i = 0; i < n; i++) {
			//Circular
			if(i == n-1) {
				next = 0;					
			}else {
				next = i+1;
			}
			
			Cp += qS0.get(i)*qS0.get(next);
		}
		
		results[1] =  Cp/n;		
		return results;		
	}
	
	//Thread run method override
	@Override
	public void run() {	
		
		//metropolis algorithm loop
		for(int iter = 0; iter < Nm; iter++) {
			mTot += metropolis(n,B,C,T,Nf)[0];
			CpTot += metropolis(n,B,C,T,Nf)[1];
			
		}
		
		//Final thread values for <m> and <Cp>
		mTot = mTot / Nm;
		CpTot = CpTot / Nm;	
				
	}
}
