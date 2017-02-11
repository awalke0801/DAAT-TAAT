import java.util.*;
import java.nio.file.*;
import java.io.*;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
//import org.apache.lucene.document.Document;

class project2{
	//GetPostings Method
	public static void GetPostings(HashMap hmap,List l, String args)throws Exception{
		File filew = new File(args);
			FileWriter fw = new FileWriter(filew,true);
			BufferedWriter bw = new BufferedWriter(fw);
		for(int i = 0;i<l.size();i++){
			bw.write("GetPostings"+"\n");
			bw.write(l.get(i)+"\n");
			bw.write("Postings list: ");
			
			Iterator l1 = ((LinkedList<Integer>)hmap.get(l.get(i))).iterator();
			while(l1.hasNext()){
				bw.write(l1.next()+" ");
			}
			bw.write("\n");
		}
		bw.close();
	}
	//Term at a time Or implementation
	public static void TaatOr(HashMap hmap,List l, String args)throws Exception{
		File filew = new File(args);
		FileWriter fw = new FileWriter(filew,true);
		BufferedWriter bw = new BufferedWriter(fw);
		int comp = 0;
		int i=0;
		int j=0;		
		LinkedList<Integer> partial = new LinkedList<Integer>();
		partial = (LinkedList<Integer>)hmap.get(l.get(0));
		//Comparing 2 lists at a time and generating an intermediate list
		for(int k = 1;k<l.size();k++){
			i=0;
			j=0;
			LinkedList<Integer> current = new LinkedList<Integer>();
			current = (LinkedList<Integer>)hmap.get(l.get(k));
			LinkedList<Integer> output = new LinkedList<Integer>();
			while(i<partial.size()&&j<current.size()){
				if((int)partial.get(i)<(int)current.get(j)){
					comp++;
					output.add(partial.get(i));
					i++;
				}
				else if((int)partial.get(i)>(int)current.get(j)){
					comp++;
					output.add(current.get(j));
					j++;
				}
				else if((int)partial.get(i)==(int)current.get(j)){
					comp++;
					output.add(partial.get(i));
					i++;
					j++;
				}
			}
			//Adding the remaining elements if any 
			while(i<(int)partial.size()){
				output.add(partial.get(i));
				i++;
			}
			while(j<(int)current.size()){
				output.add(current.get(j));
				j++;
			}
			partial = output;
		}
		//Printing the results
		bw.write("TaatOr"+"\n");	
		for(int k = 0;k<l.size();k++){
			bw.write(l.get(k)+" ");
		}
		bw.write("\n");
		bw.write("Results: ");
		if(partial.isEmpty()){
			bw.write("empty");
		}
		Iterator o1 = partial.iterator();
		while(o1.hasNext()){
			bw.write(o1.next()+" ");
		}
		bw.write("\n");
		bw.write("Number of documents in results: "+partial.size()+"\n");
		bw.write("Number of comparisons: "+comp+"\n");
		bw.close();
	}
	//Term at a time AND implementation
	public static void TaatAnd(HashMap hmap,List l, String args)throws Exception{
		File filew = new File(args);
		FileWriter fw = new FileWriter(filew,true);
		BufferedWriter bw = new BufferedWriter(fw);
		int comp = 0;
		int i=0;
		int j=0;
		LinkedList<Integer> partial = new LinkedList<Integer>();
		partial = (LinkedList<Integer>)hmap.get(l.get(0));
		//Comparing 2 lists at a time and generating an intermediate list
		for(int k = 1;k<l.size();k++){
			i=0;
			j=0;
			LinkedList<Integer> current = new LinkedList<Integer>();
			current = (LinkedList<Integer>)hmap.get(l.get(k));
			LinkedList<Integer> output = new LinkedList<Integer>();
			while(i<partial.size() && j<current.size()){
				if((int)partial.get(i)<(int)current.get(j)){
					comp++;
					i++;
				}
				else if((int)partial.get(i)>(int)current.get(j)){
					comp++;
					j++;
				}
				else if((int)partial.get(i)==(int)current.get(j)){
					comp++;
					output.add(partial.get(i));
					i++;
					j++;
				}
			}
			partial = output;
			
		}
			//Printing the results
			bw.write("TaatAnd"+"\n");
			for(int k = 0;k<l.size();k++){
				bw.write(l.get(k)+" ");
			}
			bw.write("\n");
			bw.write("Results: ");
			if(partial.isEmpty()){
				bw.write("empty");
			}
			Iterator o1 = partial.iterator();
			while(o1.hasNext()){
				bw.write(o1.next()+" ");
			}
			bw.write("\n");
			bw.write("Number of documents in results: "+partial.size()+"\n");
			bw.write("Number of comparisons: "+comp+"\n");
			bw.close();
	}
	//Method used for cloning objects 
	public static Object deepClone(Object obj)throws Exception{
		try{
			ByteArrayOutputStream baa = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(baa);
			os.writeObject(obj);
			ByteArrayInputStream bai = new ByteArrayInputStream(baa.toByteArray());
			ObjectInputStream is = new ObjectInputStream(bai);
			return is.readObject();
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	//Document at a time Or Implementation 
	public static void DaatOr(HashMap hmap, List l, int numD, String args)throws Exception{
		File filew = new File(args);
		FileWriter fw = new FileWriter(filew,true);
		BufferedWriter bw = new BufferedWriter(fw);
		LinkedList<Integer> output = new LinkedList<Integer>();
		int comp = 0;
		HashMap<String, LinkedList> nmap = new HashMap<String, LinkedList>();
		//Creating a hashmap of query term postings
		for(int k = 0; k<l.size();k++){
			LinkedList<Integer> temp = new LinkedList<Integer>();
			temp =(LinkedList<Integer>)deepClone(hmap.get(l.get(k)));
			String wor = (String)(l.get(k));
			nmap.put(wor,temp);
		}
		//Finding all the documents in the postings
		LinkedList<Integer> con = new LinkedList<Integer>();
		for(int z=0;z<l.size();z++){
			con.addAll(nmap.get(l.get(z)));
		}
		Collections.sort(con);
		boolean check = false;
		int docc;
		int min = con.size();
		//Creating the result by comparing all query term postings with the document list found 
		for(int i = 0;i<min;i++){
			docc = con.get(i);
			for(int j = 0;j<l.size();j++){
				comp++;
				if(!((nmap.get(l.get(j))).isEmpty())){
					if(((int)(nmap.get(l.get(j))).peek() == docc)){
						if(check == false){
							output.add(docc);
						}
						check = true;
						(nmap.get(l.get(j))).remove();
					}
				}
			}
			check = false;
		}
		//Printing the results 
		bw.write("DaatOr"+"\n");
		for(int k=0;k<l.size();k++){
			bw.write(l.get(k)+" ");
		}
		bw.write("\n");
		bw.write("Results: ");
		if(output.isEmpty()){
			bw.write("empty");
		}
		Iterator o1 = output.iterator();
		while(o1.hasNext()){
			bw.write(o1.next()+" ");
		}
		bw.write("\n");
		bw.write("Number of documents in results: "+output.size()+"\n");
		bw.write("Number of comparisons: "+comp+"\n");
		bw.close();
		
	}
	//Document at a time AND implementation 
	public static void DaatAnd(HashMap hmap, List l, int numD, String args)throws Exception{
		File filew = new File(args);
		FileWriter fw = new FileWriter(filew,true);
		BufferedWriter bw = new BufferedWriter(fw);
		LinkedList<Integer> output = new LinkedList<Integer>();
		int comp = 0;
		HashMap<String, LinkedList> kmap = new HashMap<String, LinkedList>();
		//Creating a hashmap of query term postings
		for(int k = 0; k<l.size();k++){
			LinkedList<Integer> temp = new LinkedList<Integer>();
			temp =(LinkedList<Integer>)deepClone(hmap.get(l.get(k)));
			String wor = (String)(l.get(k));
			kmap.put(wor,temp);
		}
		boolean check = false;
		boolean aaa = false;
		boolean t = false;
		int chec = 0;
		int min = (kmap.get(l.get(0))).size();
		int mini = 0;
		//Finding the smallest postings list out of the query terms
		for(int z=1;z<l.size();z++){
			if((kmap.get(l.get(z))).size()<min){
				min = (kmap.get(l.get(z))).size();
				mini = z;
			}

		}
		LinkedList<Integer> traversal = new LinkedList<Integer>();
		traversal = (LinkedList<Integer>)kmap.get(l.get(mini));
		int docc;
		min = (kmap.get(l.get(mini))).size();
		//Comparing all the query term postings with the smallest list  
		for(int i = 0;i<min;i++){
			if(!(traversal.isEmpty())){
				docc = traversal.get(0);
			}
			else{
				break;
			}
			for(int j = 0;j<l.size();j++){
				
				if(!((kmap.get(l.get(j))).isEmpty())){
					while((!((kmap.get(l.get(j))).isEmpty()))&&((int)(kmap.get(l.get(j))).peek() <docc)){
						(kmap.get(l.get(j))).remove();
						comp++;
						aaa = false;
						t = true;
					}
					if((!((kmap.get(l.get(j))).isEmpty()))&&((int)(kmap.get(l.get(j))).peek() == docc)){
						comp++;
						aaa = true;
						chec++;
						(kmap.get(l.get(j))).remove();
					}
					else{
						aaa = false;
						check = true;
					}
				}

				else{
					check=true;
					break;
				}
				t = false;
			}
			if(chec==l.size()){
				output.add(docc);
			}
			check = false;
			aaa = false;
			chec = 0;
		}
		//Printing the results
		bw.write("DaatAnd"+"\n");
		for(int k = 0;k<l.size();k++){
			bw.write(l.get(k)+" ");
		}
		bw.write("\n");
		bw.write("Results: ");
		if(output.isEmpty()){
			bw.write("empty");
		}
		Iterator o1 = output.iterator();
		while(o1.hasNext()){
			bw.write(o1.next()+" ");
		}
		bw.write("\n");
		bw.write("Number of documents in results: "+output.size()+"\n");
		bw.write("Number of comparisons: "+comp+"\n");
		bw.close();
		
	}
	
	public static void main(String[] args)throws Exception{
		Path p = Paths.get(args[0]);
		int c = 0;
		int noD = 0;
		String argss = args[1];
		HashMap<Integer, List> queries = new HashMap<Integer, List>();
		int querycount = 0; 
		//Accepting the query terms from the input file
		try{
			
			File filedir = new File(args[2]);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filedir),"UTF8"));
			String str = null;
			while((str=br.readLine())!=null){
				String[] tokens = str.split(" ");
				List<String> words = new ArrayList<String>();
				for(int i=0;i<tokens.length;i++){
					words.add(tokens[i]);
				}	
				querycount++;
				queries.put(querycount,words);
			}
			br.close();
		}
		catch(IOException e){
	//		bw.write("File not Found");
		}
		File filew = new File(args[1]);
		FileWriter fw = new FileWriter(filew);
		BufferedWriter bw = new BufferedWriter(fw);
		HashMap<String, LinkedList> hmap = new HashMap<String, LinkedList>();
		//Creating the postings list
		try{
			IndexReader r = DirectoryReader.open(FSDirectory.open(p));
			noD = r.numDocs();
			Collection<String> f = MultiFields.getIndexedFields(r);
			f.remove("_version_");
			f.remove("id");
			for(String text:f){	
				Terms term = MultiFields.getTerms(r,text);
				TermsEnum termite = term.iterator();
				BytesRef br;
				while((br = termite.next())!=null){
					String word = br.utf8ToString();
					c++;
					//int tf = r.docFreq()
					PostingsEnum poste = MultiFields.getTermDocsEnum(r,text,br,PostingsEnum.FREQS);
					int i;
					LinkedList<Integer> linkedlist = new LinkedList<Integer>();
					while((i = poste.nextDoc())!= PostingsEnum.NO_MORE_DOCS){
						//Document doc = r.document(i);
						int doci = poste.docID();
						linkedlist.add(doci);
						int freq = poste.freq();						
					}
					hmap.put(word, linkedlist);
				}

			}
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		//Calling all the methods for every query set 
		int k = 1;
		while(k<=querycount){
			List<String> currentquerries = new ArrayList<String>();
			currentquerries = queries.get(k);
			GetPostings(hmap, currentquerries, argss);
			TaatAnd(hmap, currentquerries, argss);
			TaatOr(hmap, currentquerries, argss);
			DaatAnd(hmap, currentquerries, noD, argss);
			DaatOr(hmap, currentquerries, noD, argss);
			k++;
		}
	}	
}