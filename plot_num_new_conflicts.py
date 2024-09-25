from mpl_toolkits import mplot3d
# %matplotlib inline
import numpy as np
import matplotlib.pyplot as plt

import simplejson


with open('results/res_num_new_conflicts.json', 'r') as f:
    res = simplejson.load(f)
    
    rs = sorted(list(set([x['safetyZone'] for x in res])))
    ns = sorted(list(set([x['n'] for x in res])))

    Z = np.zeros([len(ns), len(rs)])

    for x in res:
        r = rs.index(x['safetyZone'])
        n = ns.index(x['n'])

        Z[n, r] = x['numOfNewConflicts']/x['numberOfDronesToTest']

ax = plt.axes(projection='3d')
X, Y = np.meshgrid(rs, ns)
ax.plot_surface(X, Y, Z, rstride=1, cstride=1,
                cmap='viridis', edgecolor='none')
ax.set_ylabel('Number of drones over a day')
ax.set_xlabel('Safety radius')
ax.set_zlabel('P')

ax.view_init(elev=31, azim=-133)

plt.savefig('results/res_num_new_conflicts.png')
# plt.show()