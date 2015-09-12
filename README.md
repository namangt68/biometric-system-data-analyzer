####The Biometric System Data Analyzer

Any biometric matching algorithm can take two images (test image and gallery/database image) as
an input and return a score (dissimilarity in our case) as the output. Such a matcher is used to
perform all inter-session matching over a huge iris database and the corresponding result is
provided in the form of a text file. The format of the file is as follows:

Each row corresponds to a single matching characterized by six tuples :

1. subject_id1, Subject id of the test image.
2. pos_id1, Pose id of the test image.
3. subject_id2, Subject id of the gallery image.
4. pos_id2, Pose id of the gallery image.
5. Genuine/Imposter, 1 = Genuine, 0 = Imposter.
6. Dissimilarity Score, The dissimilarity score obtained when test is matched with gallery image.

#####How to Run
**Execute the main file directly or through shell.**
The perfomance parameters with stats of a Biometric System Analyzer are calculated .

This repository contains the code for the first asssignment of the CS 307 course. By the team of Abhishek Pandey, Swapnil Sharma and Naman Gupta.
