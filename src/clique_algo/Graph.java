package clique_algo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.ArrayList;


/**
 * this class represents an undirected 0/1 sparse Graph 
 * @author Boaz
 *
 */
 class Graph {
	 private String _file_name;
	 private ArrayList <VertexSet> _V;
	 private double _TH; // the threshold value
	 private int _E_size = 0;
	 private boolean _mat_flag=true;
	 Graph(String file, double th) {
		this._file_name = file;
		_TH = th;
		_V = new  ArrayList <VertexSet>();
		 init();
	 }
	 
	private void init() {
		
            if(this._file_name.endsWith(".dot"))
            {
                System.out.println(".dot");
                readFromDotFile();    
            }
            else{
            FileReader fr=null;
		try {
			fr = new FileReader(this._file_name);
		} catch (FileNotFoundException e) {	e.printStackTrace();}
		BufferedReader is = new BufferedReader(fr);
		try {
			String s = is.readLine();
			StringTokenizer st = new StringTokenizer(s,", ");
			int len = st.countTokens();
			int line = 0;
			
			String ll = "0%   20%   40%   60%   80%   100%";
			int t = Math.max(1,len/ll.length());
			if(Clique_TesterO.Debug){
				System.out.println("Reading a corrolation matrix of size: "+len+"*"+len+" this may take a while");
				System.out.println(ll);
			}
			_mat_flag = true;
			if (s.startsWith("A")) {
				if(Clique_TesterO.Debug){
					System.out.println("Assumes compact representation! two line haeder!!!");
					System.out.println("Header Line1: "+s);
					s = is.readLine();
					System.out.println("Header Line2: "+s);
					s = is.readLine();
					st = new StringTokenizer(s,", ");
					_mat_flag = false;
				}
			}
	
			while(s!=null) {
				
				if(Clique_TesterO.Debug){
					if(line%t==0) System.out.print(".");                                
				}
				VertexSet vs = new VertexSet();
				if(_mat_flag){
					for(int i=0;i<len;i++) {
						float v = new Double(st.nextToken()).floatValue();
						if(v>_TH & line< i) {
							vs.add(i);
							_E_size++;
						}
					}
				}
				else {
					st.nextToken();
					while(st.hasMoreTokens()) {
						int ind = new Integer(st.nextToken()).intValue();
						// bug fixed as for Ronens format.
						if(line<ind) vs.add(ind);
					}
				}
				this._V.add(vs);
				line++;
				s = is.readLine();
			if(s!=null)	st = new StringTokenizer(s,", ");
			}
			if(this._mat_flag & Clique_TesterO.Convert) {write2file();}
			if(Clique_TesterO.Debug){
				System.out.println("");
				System.out.print("done reading the graph! ");
				this.print();}
		} catch (IOException e) {e.printStackTrace();}
	 }
        }
	
	public VertexSet Ni(int i) {
		VertexSet ans = _V.get(i);
		return  ans;
	}
	public void print() {
		System.out.println("Graph: |V|="+this._V.size()+" ,  |E|="+_E_size);
		
	}
	
	/*************** Clique Algorithms ******************/
	/*ArrayList<VertexSet>  All_Cliques(int Q_size) {
		ArrayList<VertexSet> ans = new ArrayList<VertexSet>();
		ArrayList<VertexSet>C0 = allEdges(); // all edges � all cliques of size 2/
		ans.addAll(C0);
		for(int i=3;i<=Q_size;i++) {
			ArrayList<VertexSet>C1 = allC(C0);
			ans.addAll(C1);
			C0 = C1;
		} // for
		return ans;
	}
	ArrayList<VertexSet>  All_Cliques(int min_Q_size, int max_Q_size) {
		ArrayList<VertexSet> ans = new ArrayList<VertexSet>();
		ArrayList<VertexSet> C0 = allEdges(), C1=null; // all edges � all cliques of size 2/
		for(int i=0;i<C0.size();i++) {
			VertexSet curr = C0.get(i);
			C1 = All_Cliques_of_edge(curr, min_Q_size,  max_Q_size);
//			System.out.println("Edge: ["+curr.at(0)+","+curr.at(1)+"]");
			ans.addAll(C1);
		}
		return ans;
	}*/
	/**
	 * this method retuns all the Cliques of size between [min,max] which contains the subVertexSet e (usually an edge);
	 * @param min_Q_size
	 * @param max_Q_size
	 * @return
	 */
	/*
	ArrayList<VertexSet>  All_Cliques_of_edge(VertexSet e, int min_Q_size, int max_Q_size) {
		ArrayList<VertexSet> ans = new ArrayList<VertexSet>();
		ans.add(e);
		int i=0;
		int last_size = e.size();
		while(i<ans.size() & last_size <=max_Q_size) {
			VertexSet curr = ans.get(i);
			VertexSet inter = intersection(curr);
			addbiggerCliQ(ans,curr,inter);
			last_size = ans.get(ans.size()-1).size(); 
			i++;
		}
		int start = 0; i=0;
		while(i<ans.size() && start==0) {
			if(ans.get(i).size()<min_Q_size) {ans.remove(0);}
			else start=1;
			i++;
		}
		return ans;
	}
	ArrayList<VertexSet> allC(ArrayList<VertexSet> C0) {
		ArrayList<VertexSet> ans = new ArrayList<VertexSet>();
		for(int i=0;i<C0.size();i++) {
			VertexSet curr = C0.get(i);
			VertexSet inter = intersection(curr);
			if(inter.size()>0)  
				addbiggerCliQ(ans,curr,inter); // strange clique expqnding function
	}	
		return ans;	
	}
	VertexSet intersection(VertexSet C) {
		VertexSet ans = _V.get(C.at(0));
		for(int i=0;ans.size()>0 & i<C.size();i++) 
			ans = ans.intersection(_V.get(C.at(i)));
		return ans;
	}
	private void addbiggerCliQ(ArrayList<VertexSet> ans,VertexSet curr ,VertexSet inter) {
		int last = curr.at(curr.size()-1);
		for(int i=0;i<inter.size();i++) {
			int ind_inter = inter.at(i);
			if(last<ind_inter) {
				VertexSet c = new VertexSet(curr);
				c.add(ind_inter);
				ans.add(c);
			}
		}
	}
	private ArrayList<VertexSet> addbiggerCliQ(VertexSet curr ,VertexSet inter) {
		ArrayList<VertexSet> ans = new ArrayList<VertexSet>(inter.size());
		int last = curr.at(curr.size()-1); // last vertex in the current clique (ordered!)
		for(int i=0;i<inter.size();i++) {
			int ind_inter = inter.at(i);
			if(last<ind_inter) {
				VertexSet c = new VertexSet(curr);
				c.add(ind_inter);
				ans.add(c);
			}
		}
		return ans;
	}*/
	/**
	 * computes all the 2 cliques --> i.e. all the edges 
	 * @return
	 */
	private ArrayList<VertexSet> allEdges() { // all edges � all cliques of size 2/
		ArrayList<VertexSet> ans = new ArrayList<VertexSet>();
		for(int i=0;i<_V.size();i++) {
			VertexSet curr = _V.get(i);
			for(int a=0;a<curr.size();a++) {
				if(i<curr.at(a)) {
					VertexSet tmp = new VertexSet();
					tmp.add(i) ; 
					tmp.add(curr.at(a));
					ans.add(tmp);
				}
			}
			
		}
		return ans;
	}
	/**
	 * This method computes all cliques of size [min,max] or less using a memory efficient DFS like algorithm.
	 * The implementation was written with CUDA in mind - as a based code for a possibly implementation of parallel cernal.
	 * 
	 */
	ArrayList<VertexSet>  All_Cliques_DFS(int min_size, int max_size) {
		Clique.init(this);
		ArrayList<VertexSet> ans = new ArrayList<VertexSet>();
		ArrayList<VertexSet>C0 = allEdges(); // all edges � all cliques of size 2/
	//	ans.addAll(C0);
		int len = C0.size();
		//System.out.println("|E|= "+len);
		int count = 0;
		for(int i=0;i<len;i++) {
			
			VertexSet curr_edge = C0.get(i);
			Clique edge = new Clique(curr_edge.at(0),curr_edge.at(1) );
			ArrayList<Clique> C1 = allC_seed(edge, min_size, max_size);
			count+=C1.size();
			//System.out.println("alg2 "+i+") edge:["+curr_edge.at(0)+","+curr_edge.at(1)+"]"+C1.size() +"  total: "+count);
			addToSet(ans, C1);
		} // for
		return ans;
	}
	/**
	 * 
	 * @param min_size
	 * @param max_size
	 */
	 public void All_Cliques_DFS(String out_file, int min_size, int max_size) {
			Clique.init(this);
			ArrayList<VertexSet>C0 = allEdges(); // all edges � all cliques of size 2/
			int len = C0.size();
			System.out.println("|E|= "+len);
			int count = 0;
			
			FileWriter fw=null;
			try {fw = new FileWriter(out_file);} 
			catch (IOException e) {e.printStackTrace();}
			PrintWriter os = new PrintWriter(fw);
			//os.println("A");
			
			String ll = "0%   20%   40%   60%   80%   100%";
			int t = Math.max(1,len/ll.length());
			if(Clique_TesterO.Debug){
				System.out.println("Computing all cliques of size["+min_size+","+max_size+"] based on "+len+" edges graph, this may take a while");
				System.out.println(ll);
			}
			os.println("All Cliques: file [min max] TH,"+this._file_name+","+min_size+", "+max_size+", "+this._TH);
			os.println("index, edge, clique size, c0, c1, c2, c3, c4,  c5, c6, c7, c8, c9");
			for(int i=0;i<len;i++) {
				
				VertexSet curr_edge = C0.get(i);
				Clique edge = new Clique(curr_edge.at(0),curr_edge.at(1) );
				ArrayList<Clique> C1 = allC_seed(edge, min_size, max_size);
			
				
				for(int b=0;b<C1.size();b++) {
					Clique c = C1.get(b);
					if (c.size()>=min_size) {
						os.println(count+", "+i+","+c.size()+", "+c.toFile());
						count++;
					}
				}
				if(count > Clique_TesterO.MAX_CLIQUE) {
					os.println("ERROR: too many cliques! - cutting off at "+Clique_TesterO.MAX_CLIQUE+" for larger files change the default Clique_Tester.MAX_CLIQUE param");
					i=len;
				}
				if(i%t==0) {
					System.out.print(".");
				}
			} // for
			System.out.println();
			
			os.close();
			try {
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
	/**
	 * this function simply add the clique (with no added intersection data) to the set of cliques)
	 * @param ans
	 * @param C1
	 */
	private void addToSet(ArrayList<VertexSet> ans, ArrayList<Clique> C1) {
		for(int i=0;i<C1.size();i++) {
			ans.add(C1.get(i).clique());
		}
	}
	ArrayList<Clique> allC_seed(Clique edge, int min_size, int max_size) {
		ArrayList<Clique> ans = new ArrayList<Clique>();
		ans.add(edge);
		int i=0;
	//	int size = 2;
		while (ans.size()>i) {
			Clique curr = ans.get(i);
			if(curr.size()<max_size) {
				VertexSet Ni = curr.commonNi();
                                   if (Ni.size() +curr.size()>=min_size) {

				for(int a=0;a<Ni.size();a++) {
					Clique c = new Clique(curr,Ni.at(a));
					ans.add(c);
				}
                                   }
			}
			else {i=ans.size();} // speedup trick 
			i++;
		}
		
		return ans;
	}

	public void write2file() {
		FileWriter fw=null;
		try {fw = new FileWriter(this._file_name+"_DG.txt");} 
		catch (IOException e) {e.printStackTrace();}
		PrintWriter os = new PrintWriter(fw);
		os.println("ALL_Cliques: of file: "+_file_name+",  TH:"+this._TH);
		os.println("");
		for(int i=0;i<this._V.size();i++) {
			VertexSet curr = _V.get(i);
			os.println(i+", "+curr.toFile());
		}
		os.close();
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
        private void readFromDotFile(){
             Scanner fr=null;
             String s;
		try {
			fr = new Scanner(new File(this._file_name));
		} catch (FileNotFoundException e)
                {	e.printStackTrace();}
		
                s = fr.nextLine();
                  System.out.println("s"+s);
             int vertexInt=0;
         while (!fr.hasNext("}"))
         {
             
             int temp=fr.nextInt();
             System.out.println("temp: "+temp);
             if(temp!=vertexInt){
                   System.out.println("vertexInt: "+vertexInt);
                 VertexSet vs = new VertexSet();
                 vertexInt=temp;
                 System.out.println("vertexInt: "+vertexInt);
                 _V.add(vertexInt-1, vs);
             }
             if(fr.hasNext("--")){
                 fr.next("--");
                 
                 temp=fr.nextInt();
                  System.out.println("temp: "+temp);
             }
             else{
                 System.out.print("Error");
             }
             if(fr.hasNext("\\[weight\\=")){
                  
             fr.next("\\[weight\\=");
             float wegit=fr.nextFloat();
             System.out.println("wegit: "+wegit);
             if(wegit>=_TH){                 
               _V.get(vertexInt-1).add(temp);
             }
             }
             else{
                     System.out.println("Error2"); 
                     }
            // System.out.println("fj: ");
             fr.nextLine();
         }
         }

}