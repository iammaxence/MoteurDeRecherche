import numpy as np
import matplotlib.pyplot as plt
import torch
from torch.distributions import Bernoulli, Beta, Uniform, Poisson, Gamma, Normal, Exponential

# Utils

def plot_data(results, ax, num_bins=50):
    ax.hist(results, num_bins)


def plot_posterior(results, ax):
    gather = np.array(
        [(x, np.sum(np.array([w for (y, w) in results if y==x ])))
         for x in np.unique(results[:,0])])
    theta = gather[:, 0]
    score = gather[:, 1]
    ax.vlines(theta, 0, score)

def plot_pdf(d, xm ,xM, ax):
    x = torch.linspace(xm, xM, 1000)
    y = np.exp(d.log_prob(x))
    ax.plot(x, y)


# Plots

## make subplosts
fig, ((ax1, ax2), (ax3, ax4), (ax5, ax6)) = plt.subplots(3, 2)
## make a little extra space between the subplots
fig.subplots_adjust(hspace=0.5)
## adjust x size
ax1.set_xlim(0, 20)
ax2.set_xlim(-1, 2)
ax2.set_ylim(0, 1)
ax3.set_xlim(0, 15)
ax3.set_ylim(0, 0.5)
ax4.set_xlim(-1, 2)
ax4.set_ylim(0, 1)
ax5.set_xlim(0, 0.5)
ax6.set_ylim(0, 1)
ax6.set_xlim(-1, 2)


plt.savefig("bus.png")
plt.close()

