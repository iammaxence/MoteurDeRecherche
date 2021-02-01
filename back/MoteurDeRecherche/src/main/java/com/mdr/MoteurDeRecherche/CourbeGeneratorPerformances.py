import matplotlib.pyplot as plt

#Test de performances 
axes = [0,200,400,600,800,1000,1200,1400,1600,1800,2000]

#Algorithmes de recherche

dataClassique = [0,837,1205,1411,1781,1922,1913,2201,2290,2629,2836]
dataMultiple = [0,789,1340,1366,1763,1895,1885,2173,2477,2651,3205]
dataRegex = [0,887,1146,1315,1449,1681,2049,2400,2847,3059,3329]

# Plots
# plt.plot(dataClassique,axes,"r",label='Recherche mot : fit')
# plt.plot(dataMultiple,axes,"b",label='Recherche mots : fit,turn,role')
# plt.plot(dataRegex,axes,"g",label='Recherche regex : r(O|l|e)')
# #plt.plot(axes,axes,"pink",label='Linear')
# plt.legend()
# plt.ylabel('Nombres de livres')
# plt.xlabel('Temps (en milisecondes)')
# plt.show()

#Closeness Centrality


axes = [0,100,200,300,400,500,600,700,800,900,1000,1100,1200,1300,1400,1500] #Nb livres
dataCloseness = [0,59,141,171,188,193,200,212,204,218,214,254,304,327,343,361]

# plt.plot(dataCloseness,axes,"b+",label='Closeness Centrality')
# plt.legend()
# plt.ylabel('Nombres de livres')
# plt.xlabel('Temps (en milisecondes)')
# plt.show()

#Jaccard distances
axes = [10000, 50000,100000,150000,200000,250000,300000,350000,400000,450000,500000] #Nb livres
dataJaccardDistace = [48,83,128,169,197,265,271,286,306,343,374]
plt.plot(dataJaccardDistace,axes,"b+",label='Distances de Jaccard')
plt.legend()
plt.ylabel('Nombres de mots')
plt.xlabel('Temps (en milisecondes)')
plt.show()
# plt.savefig("RechercheAlgo.png")
# plt.close()

