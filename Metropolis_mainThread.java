public class Metropolis_mainThread {
	
	public static void initialVarMessage(int Nt, int Nf, int Nm, int n, double B, double C, double T, double cstar) {
		System.out.println("Metropolis Algorithm - Quantum spin and Thermodynamics");
		System.out.println("------------------------------------------------------");
		System.out.println("Value for Cp* = " + cstar);
		System.out.println("Number of Threads (Nt) = " + Nt);
		System.out.println("Number of flips (Nf) = " + Nf);
		System.out.println("Number of algorithm calls by thread (Nm) = " + Nm);
		System.out.println("Number of quantum spins (n) = " + n);
		System.out.println("Value for B = " + B);
		System.out.println("Value for C = " + C);
		System.out.println("Value for T = " + T);
		System.out.println("");
	}
	
	public static void progress(int Nt, int counter) {
		System.out.print("\r" + counter + " Threads completed (" + (((double)counter/Nt)*100) + "%)");
	}
	
	public static void main(String[] args) {
		int count = 0;
		int Nt = 1000;
		int Nf = 550;
		int Nm = 12;
		int n = 100;
		double B = 0;
		double C = -1;
		double T = 1.9;
		double [] m = new double [Nt];
		double [] Cp = new double [Nt];
		double mu_m = 0;
		double mu_Cp = 0;
		double relErr = 0;
		double variance_relErr = 0;
		
		//Constant variables
		//Theoretical Cp value if B = 0
		final double CPSTAR = (Math.exp(C/T) - Math.exp(-(C/T)))/(Math.exp(C/T) + Math.exp(-(C/T)));		
		
		//Set system time @0
		final long time = System.currentTimeMillis();
		
		MetropolisThread mT[] = new MetropolisThread[Nt];
		
		initialVarMessage(Nt,Nf,Nm,n,B,C,T,CPSTAR);		
		
		//Start all threads with correct parameters
		for(int i = 0; i < Nt; ++i) {
			mT[i] = new MetropolisThread(n,B,C,T,Nf,Nm);
			mT[i].start();
		}
		System.out.println("-------------------------------------------------");
		System.out.println("all threads spawned. Waiting to complete...");
		System.out.println("");
		
		//Waits for each thread to complete
		for(int i = 0; i < Nt; ++i) {
			try {
				if(mT[i].isAlive()) {
					mT[i].join();					
				}
			}
			catch(Exception e) {}			
		}
		
		//Retrieve average values for m and c from each thread, store in array for statistical analysis
		for(int j = 0; j < Nt; ++j) {
			m[j] = mT[j].mTot;
			mu_m += m[j];
			Cp[j] = mT[j].CpTot;
			mu_Cp += Cp[j];
			
			//Computing relative error and variance
			relErr += ((Cp[j] - CPSTAR)/CPSTAR);
			variance_relErr += (((Cp[j] - CPSTAR)/CPSTAR) - relErr)*(((Cp[j] - CPSTAR)/CPSTAR) - relErr);
		}
		
		mu_m = mu_m/Nt;
		mu_Cp = mu_Cp/Nt;
		relErr = relErr/Nt;
		variance_relErr = variance_relErr/Nt;
		
		System.out.println("----------------------------------------------------------------------");
		System.out.println("Thread calculations completed in " + (System.currentTimeMillis() - time) + " ms.");
		System.out.println("");
		System.out.println("Calculated value for Cp = " + mu_Cp);
		System.out.println("Calculated relative error: " + relErr);
		System.out.println("Computed variance of relative error: " + variance_relErr);
		
		System.out.println("Program completed");

	}

}
