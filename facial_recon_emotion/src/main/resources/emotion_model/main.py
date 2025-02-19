import numpy as np
import tensorflow as tf
import cv2
import sys
from tensorflow.keras.models import load_model

# Load the model
model = load_model('emotion_model.h5')

def preprocess_image(image_path, target_size=(48, 48)):
    """
    Loads an image, converts it to grayscale, resizes, normalizes, and reshapes it.
    """
    img = cv2.imread(image_path)
    if img is None:
        print("Error: Could not read the image.")
        sys.exit(1)  # Exit if image is invalid

    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)  # Convert to grayscale
    face = cv2.resize(gray, target_size)          # Resize to model's input size
    face = face / 255.0                           # Normalize pixel values
    face = np.expand_dims(face, axis=0)           # Add batch dimension
    return face

# Define the emotion labels based on your model's training
emotion_labels = ["Angry", "Disgust", "Fear", "Happy", "Neutral", "Sad", "Surprise"]

def predict_emotion(image_path):
    face = preprocess_image(image_path)
    prediction = model.predict(face)
    emotion = emotion_labels[np.argmax(prediction)]
    confidence = np.max(prediction)
    return f"{emotion} (Confidence: {confidence:.2f})"

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print("Error: No image path provided.")
        sys.exit(1)

    image_path = sys.argv[1]  # Get image path from command-line argument
    emotion_result = predict_emotion(image_path)

    # Only print the relevant output
    print(emotion_result)
