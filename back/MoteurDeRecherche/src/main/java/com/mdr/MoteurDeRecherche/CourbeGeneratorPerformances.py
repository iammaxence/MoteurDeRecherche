import matplotlib.pyplot as plt

#Test de performances 
axes = [0,200,400,600,800,1000,1200,1400,1600,1800,2000]

#Algorithmes de recherche
dataClassique = [0,394,730,744,943,1087,1628,1687,1899,1849,1858]
dataMultiple = [0,398,767,725,932,1101,1703,1800,1852,1867,2421]
dataRegex = [0,408,641,714,1374,1237,1753,1921,1985,1946,2156]


# Plots
plt.plot(dataClassique,axes,"r",label='Recherche mot : fit')
plt.plot(dataMultiple,axes,"b",label='Recherche mots : fit,turn,role')
plt.plot(dataRegex,axes,"g",label='Recherche regex : r(O|l|e)')
plt.plot(axes,axes,"pink",label='Linear')
plt.legend()
plt.ylabel('Nombres de livres')
plt.xlabel('Temps (en milisecondes)')
plt.show()







plt.show()
# plt.savefig("RechercheAlgo.png")
# plt.close()

