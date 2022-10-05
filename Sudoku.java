package poo.progetti;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import poo.backtracking.Backtracking;
import poo.backtracking.BacktrackingNigro;

class CellaS{
	int i,j;
	public CellaS(int i, int j) {
		this.i=i;
		this.j=j;
	}
}

public class Sudoku extends Backtracking<CellaS,Integer> {

	private int[][] griglia =new int[9][9];
	private int[][] startSet =new int[9][9]; //griglia che contiene tutti le celle inizialmente impostate dall'utente
	private int counter=0;
	private int maxSol=1;
	
	public Sudoku() {
		for(int i=0; i<griglia.length; i++) {
			for(int j=0; j<griglia[0].length; j++) {
				griglia[i][j]=0;
				startSet[i][j]=0;
			}
		}
	}

	public void imposta(int i, int j, int v) {
		if(i<0||i>8 || j<0||j>8 || v<1||v>9) {
			System.out.println("\tposizione e/o valore non validi");return;
		}
		
		CellaS c = new CellaS(i,j);
		//seconda condizione necessaria perche il caso di sovrascrittura del numero impostato il metodo assegnabile restituisce true
		if(assegnabile(c,v) && griglia[i][j]==0) { 
			assegna(c,v);
			startSet[i][j]=v;
		}
		else
			System.out.println("\tnon � possibile inserire "+v+" in posizione ("+i+","+j+")");
	}
	//metodo per consentire all'utente di scegliere il numero di soluzioni da stampare
	public void setMaxSol(int x) {
		this.maxSol = x;
	}

	@Override
	protected boolean assegnabile(CellaS p, Integer s) {
		//cella gi� occupata, ma impostata dall'utente, quindi restituisce true per non bloccare la ricorsione
		//questo true ovviamente sar� preso in considerazione anche dal metodo assegna e deassegna
		if(griglia[p.i][p.j]!=0 && griglia[p.i][p.j] == startSet[p.i][p.j]) return true;
		
		//cella gi� occupata (impostata);
		if(griglia[p.i][p.j]!=0) return false;
		
		
		//stessa riga o stessa colonna
		for(int i=0; i<griglia.length; i++)
			if(griglia[p.i][i] == s || griglia[i][p.j] == s)
				return false;
		
		//stessa sotto griglia 3x3 
		/*	dividendo per 3 qualsiasi indice della griglia si ottiene 0,1 o 2 moltiplicando per 3 si ha
		 	uno dei punti di partenza proposti nella traccia:
		 	<0,0>, <0,3>, <0,6>, <3,0>, <3,3>, <3,6>,<6,0>, <6,3>, <6,6>
		*/
		int subRow = (p.i/3)*3;//indice di riga di partenza della sottogriglia
		int subCol = (p.j/3)*3;//indice di colonna di partenza della sottogriglia
		
		for(int i=subRow; i<subRow+3; i++)
			for(int j=subCol; j<subCol+3; j++) 
				if(griglia[i][j]==s)
					return false;
		
		return true;
	}

	@Override
	protected void assegna(CellaS ps, Integer s) {
		//cella impostata dall'utente, non viene assegnata per questo motivo.
		//scelta ovviamente presa in considerazione anche dal metodo deassegna e assegnabile
		if(griglia[ps.i][ps.j]!=0 && griglia[ps.i][ps.j] == startSet[ps.i][ps.j]) return;
		
		griglia[ps.i][ps.j] = s;
	}

	@Override
	protected void deassegna(CellaS ps, Integer s) {
		//cella impostata dall'utente, non viene deassegnata per questo motivo.
		//scelta ovviamente presa in considerazione anche dal metodo assegna e assegnabile
		if(griglia[ps.i][ps.j]!=0 && griglia[ps.i][ps.j] == startSet[ps.i][ps.j]) return;
		
		griglia[ps.i][ps.j] = 0;
	}

	@Override
	protected void scriviSoluzione(CellaS cellaS) {
		for (int i = 0; i < griglia.length; i++) {
			for (int j = 0; j < griglia[0].length; j++) {
				System.out.print(griglia[i][j]+" ");
			}
			System.out.println("");
		}
		System.out.println("####################");
	}


	@Override
	protected CellaS prossimoPuntoDiScelta(CellaS p) {
		/*se il punto di scelta attuale non si trova sul bordo destro della griglia, si posiziona nella cella con
		 * l'indice di colonna successivo. Altrimenti si posiziona nella cella con indice di riga successivo e
		 * indice di colonna 0
		*/
		if (p.j<griglia[0].length-1)
			return new CellaS(p.i,p.j+1);
		return new CellaS(p.i+1,0);
	}

	@Override
	protected boolean esisteProssimoPuntoDiScelta(CellaS p) {
		/*se il punto di scelta attuale non si trova sul bordo destro della griglia ne esiste sicuramente uno successivo
		 * altrimenti se si trova sul bordo destro ma ci sono ancora righe al di sotto non analizzate esiste un successivo
		 * se si trova sull'angolo in basso a destra, non esiste alcun successivo.
		*/ 
		if (p.j<griglia[0].length-1)
			return true;
		if (p.i<griglia.length-1)
			return true;
		return false;
	}

	@Override
	protected Collection<Integer> scelte(CellaS p) {
		Collection<Integer> s = new ArrayList<>();
		for(int i=1; i<=9; i++)
			s.add(i);
		return s;
	}

	@Override
	protected boolean esisteSoluzione(CellaS cellaS) {
		for (int i = 0; i < griglia.length; i++)
			for (int j = 0; j < griglia[0].length; j++)
				if (griglia[i][j]==0)
					return false;
		counter ++;
		return true;
	}

	@Override
	protected boolean ultimaSoluzione(CellaS cellaS) {
		return counter==maxSol; //si ferma quando si raggiungono maxSol soluzioni
	}

	@Override
	protected List<CellaS> puntiDiScelta() {
		return null;
	}

	public static void main(String[] args) {
		Sudoku s = new Sudoku();
		Scanner sc = new Scanner(System.in);
		System.out.print("Vuoi impostare una cella? [1:si,0:no] : ");
		
		//ciclo per inserire delle celle statiche che non verranno toccate dall'algoritmo.
		if(sc.nextInt()==1) {
			System.out.println("\n  inserisci le coordinate e il valore della cella \"i,j,val\" [\"*\" per interrompere] : ");
			while(true){			
				System.out.print("  ");
				String c = sc.next();
				if(c.equals("*")) break;
				String[] cella = c.split(",");
				s.imposta(Integer.parseInt(cella[0]), Integer.parseInt(cella[1]), Integer.parseInt(cella[2]));
			}
			System.out.println("\nGriglia Attuale:");
			s.scriviSoluzione();
		}
	
		System.out.print("\nQuante soluzioni vuoi stampare ? ");
		s.setMaxSol(sc.nextInt());
		System.out.println("\n   soluzione :");
		s.risolvi(new CellaS(0,0));
		// TODO Auto-generated method stub
	}
}
