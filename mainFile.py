# This is the main implementation file

import sys, collections

# The histogram recording the imposter(G/T), genuine(G/T),
# values for different classifier values
a = {}

# Sum of genuine to optimize the code by decreasing the loop count
sumGenuine = 0

# Open file at given location
with open('L4_1000.txt', 'r') as f:
	
	# Iterate through each line of file
	for line in f:

		# Split the line for parsing
		line = line.rstrip('\n').split('\t')
		
		# Convert the data type of Ground truth and Classifier score
		# Delete all else
		del line[0:4]
		line[0] = int(line[0])
		line[1] = float(line[1])

		# Try to find the key and increase its value by 1
		# else, create a new key-data pair and set to 1
		# The precision in case of index can be varied
		if line[0] == 1:
			
			sumGenuine += 1
			
			try:
				a[int(line[1]*(10**6))]["Gequalto"] += 1
			except:
				a[int(line[1]*(10**6))] = {}
				a[int(line[1]*(10**6))]["Gequalto"] = 1
				a[int(line[1]*(10**6))]["Iequalto"] = 0
		else:
			try:
				a[int(line[1]*(10**6))]["Iequalto"] += 1
			except:
				a[int(line[1]*(10**6))] = {}
				a[int(line[1]*(10**6))]["Iequalto"] = 1
				a[int(line[1]*(10**6))]["Gequalto"] = 0

# Loop to calculate cumulative imposter and genuine count
Ipre, Gpre, minAbsDiff, threshold = 0, 0, float("inf"), -1
for key in sorted(a):
	Ipre += a[key]["Iequalto"]
	a[key]["Iequalto"] = Ipre - a[key]["Iequalto"]

	Gpre += a[key]["Gequalto"]
	a[key]["Gequalto"] = sumGenuine - Gpre + a[key]["Gequalto"]

	# Subject for calculation of difference between FA and FR
	# To minimize the difference
	absDiff = abs(a[key]["Iequalto"] - a[key]["Gequalto"])
	# Calculate minimum distance between FAR and FRR plot
	# and save the minAbsDiff & threshold (ERR)
	if minAbsDiff > absDiff:
		minAbsDiff = absDiff
		threshold = key

print "Threshold  is given by (EER):", threshold*(10**(-6))

# Sort the histogram created
a = collections.OrderedDict(sorted(a.items()))

print a