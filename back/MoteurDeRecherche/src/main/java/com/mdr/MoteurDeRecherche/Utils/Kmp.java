package com.mdr.MoteurDeRecherche.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Kmp {
	
    // Driver program to test above function 
    public static void main(String args[])
    { 
        String txt = "ABABAABABA"; 
        String pat = "ABAAB"; 
        System.out.println(kmpAlgorithmSeq(pat, txt));
    } 
	
	/**
	 * L'algorithme de KMP (cf : https://www.youtube.com/watch?v=GTJr8OvyEVQ) 
	 * @param regex : Le mot recherché dans le livre
	 * @param texte : Le livre
	 * @return Renvoie une liste contenant les lignes qui match avec la regex en parametre
	 * @throws IOException
	 */
	public static ArrayList<String> kmpAlgorithm(String regex,File texte ) throws IOException { //Complexité en O(n+m)* O(k) avec n= Prépocessus , m = parcours des caracteres et k = parcours des lignes du livre 
		
		char [] facteur=regex.toCharArray(); //Ma regex sous la forme d'un tableau de caractère (pour la comparaison)
		int [] retenue= retenue(facteur); //Pré-processus pour obtenir le carryOver
		String line; // Les lignes de mon texte
		int pos=0; // Un pointeur vers un des caractere du facteur ( facteur = mot recherché)
		ArrayList<String> res= new ArrayList<String>(); //Le résultats du matching
		
		BufferedReader buffer = new BufferedReader(new FileReader(texte));
		
		while((line = buffer.readLine()) != null) {
			char [] ln= line.toCharArray();
			
			for(char c: ln) {
				
				if(pos>=regex.length()) {
					res.add(line);
					break;
				}
				if(c!= facteur[pos]) {
					int i= pos-1 < 0 ? 0: pos-1;
					pos= retenue[i];
				}
				else
					pos++;
			}
			pos=0;
		}
		return res;
	}

	public static ArrayList<String> kmpAlgorithm2(String regex,File texte ) throws IOException { //Complexité en O(n+m)* O(k) avec n= Prépocessus , m = parcours des caracteres et k = parcours des lignes du livre 
		
		ArrayList<String> matchs = new ArrayList<>();
		BufferedReader buffer = new BufferedReader(new FileReader(texte));
		String line;
		int cpt=0;
		int res;
		while((line = buffer.readLine()) != null) {
			res = kmpAlgorithmSeq(regex, line);
			if (res>0)
				matchs.add(line);
		}		
		return matchs;
	}
	
	public static int kmpAlgorithmSeq(
			String regex,
			String seq
		)
	{ 
		
		char [] facteur = regex.toCharArray();
		int [] retenues = retenue(facteur);

		int pos = 0;
		int cpt = 0;
        int i = 0;
        
        while (i < seq.length()-1) { 
            if (seq.charAt(i) == facteur[pos]) { 
                pos++; 
                i++; 
            } 
            if (pos == regex.length()) {
            	cpt++;
                pos = retenues[pos - 1]; 
            } 
            else if (i < seq.length()-1 && seq.charAt(i) != facteur[pos]) { 
                if (pos != 0) 
                    pos = retenues[pos - 1]; 
                else
                    i++; 
            } 
        }

		return cpt;
	}
	
	/**
	 * 
	 * @param regex : Le mot recherché
	 * @return Renvoie la retenue pour la regex en parametre
	 */
	public static int [] retenue(char[] regex){ // Complexité en temps est en O(n)
		
		//La retenue pour la regex en parametre
		int retenue[] = new int[regex.length];
		
		//Dans le cas où le mot est vide (="")
		if(regex.length<=0)
			return retenue;
		
		retenue[0]=0; //Le premier élément à toujours pour retenue 0 (C'est notre point de départ)
		
		//Les pointeurs
		int i=0;
		int j=1;

		while(j<regex.length) { // Je parcours les caracteres de mon mot jusqu'a que j'arrive au dernier caractere


			if(j==(regex.length-1)) { // Si on arrive au dernier caractere

				if(regex[i]!= regex[j]) { //Si le dernier caractere et le caractere pointer par i sont différents

					int v= i-1<0 ? 0: i-1; //La lettre précédente au caractere pointer par i est i-1. Si i pointe le premier caractere (i=0) alors v = 0 (sinon i-1= -1 soit une arrayboundexception)
					if(regex[retenue[v]]==regex[j]) { // Et si le caractere dont le pointeur est la retenue du caractere précédent i est égal au dernier caractere
						retenue[j] = retenue[v]+1;
						return retenue;
					}

					if(v==0) {
						retenue[j]=0;
						return retenue;
					}
					else
					{
						i=retenue[v];
						continue;
					}

				}
				else {
					retenue[j]=i+1;
					return retenue;
				}
			}

			//Si les deux caracteres sont égaux
			if(regex[i]==regex[j]) {
				retenue[j]=i+1;
				i++;
				j++;
			}
			else {
				// La boucle permet de chercher le prefix le plus proche de la lettre courante
				while (regex[i]!=regex[j] ) {
					
					if(i==0) { //Si on revient au tout début, alors il n'y a pas préfixe commun, on met 0
						retenue[j]=0;
						j++;
						break;
					}
					//Si le caractere dont le pointeur est la retenue du caractere précédent i est égal au dernier caractere
					i=i-1;
					if(regex[retenue[i]]==regex[j]) {  //Si on trouve un préfixe
						retenue[j] = retenue[i]+1;
						i++;
						j++;
						break;
					}
					i=retenue[i];
				}

			}


		}

		return retenue;
	}
	
	public static int differentsLetter(char [] regex,int [] retenue,int i,int j) {
		int v= i-1<0 ? 0: i-1;
		
		if(regex[retenue[v]]==regex[j]) {
			return retenue[v]+1;
		}
		if(i==0)
			return 0;
		
		return differentsLetter(regex, retenue, retenue[v], j);
	}
}
