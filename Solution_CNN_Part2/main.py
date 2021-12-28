from handshape_feature_extractor import HandShapeFeatureExtractor
from frameextractor import frameExtractor
from numpy import genfromtxt
from scipy import spatial

import glob
import cv2
import numpy as np
import os
import tensorflow as tf
import math


def find_gesture_number(vect, penul_layer):
    distCost = []

    for i in penul_layer:
        distCost.append(spatial.distance.cosine(vect,i))
        gestu = distCost.index(min(distCost))
        gestu=math.floor(gestu/3)
    return gestu


def getPenultimateLayer(frames_path,file_name):
    files_list = []

    path = os.path.join(frames_path,"*.png")
    frames = glob.glob(path)
    frames.sort()
    files_list = frames

    vectorPrediction = get_vectors_for_frames(files_list)
    np.savetxt(file_name, vectorPrediction, delimiter=",")



def get_vectors_for_frames(files_list):

    vec = []
    vid = []

    prediction_model = HandShapeFeatureExtractor.get_instance()

    for frame in files_list:
        img = cv2.imread(frame)
        img = cv2.rotate(img, cv2.ROTATE_180)
        img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

        results = prediction_model.extract_feature(img)
        results = np.squeeze(results)

        vec.append(results)
        vid.append(os.path.basename(frame))
    
    return vec




# =============================================================================
# Get the penultimate layer for training data
# =============================================================================
# your code goes here
# Extract the middle frame of each gesture video

folderVideo = os.path.join('traindata')
pathVideo = os.path.join(folderVideo,"*.mp4")
videoAll = glob.glob(pathVideo)

listOfVideos = []
listOfVideos = videoAll

count = 0

for video in listOfVideos:
    frames_path= os.path.join(folderVideo,"frames")
    frameExtractor(video, frames_path, count)
    count += 1

penultimateFileName = 'training_vector.csv'
frames_path = os.path.join(folderVideo,"frames")

getPenultimateLayer(frames_path, penultimateFileName)

# =============================================================================
# Get the penultimate layer for test data
# =============================================================================
# your code goes here 
# Extract the middle frame of each gesture video

folderVideo = os.path.join('test')
pathVideo = os.path.join(folderVideo,"*.mp4")
videoAll = glob.glob(pathVideo)

listOfVideos = []
listOfVideos = videoAll

count = 0

for video in listOfVideos:
    frames_path = os.path.join(folderVideo,"frames")
    frameExtractor(video, frames_path, count)
    count += 1

penultimateFileName2 = 'test_vector.csv'
frames_path = os.path.join(folderVideo,"frames")

getPenultimateLayer(frames_path, penultimateFileName2)

# =============================================================================
# Recognize the gesture (use cosine similarity for comparing the vectors)
# =============================================================================

training_data = genfromtxt(penultimateFileName, delimiter=',')
test_data = genfromtxt(penultimateFileName2, delimiter=',')

result = []

for j in test_data:
    result.append(find_gesture_number(j, training_data))

np.savetxt('Results.csv', result, delimiter=",", fmt='% d')
