import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Conv2D, MaxPooling2D, Flatten, Dense, Dropout, BatchNormalization
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.optimizers import Adam


def train_emotion_model(dataset_path, epochs=25, batch_size=32, img_size=(48, 48)):
    """
    Trains an emotion detection model using a dataset of labeled images.

    :param dataset_path: Path to the dataset directory (structured as subfolders per emotion)
    :param epochs: Number of training epochs (default=25)
    :param batch_size: Batch size for training (default=32)
    :param img_size: Target image size (default=(48, 48))

    :return: Trained Keras model
    """

    # Define image data generator with augmentation
    datagen = ImageDataGenerator(
        rescale=1.0 / 255.0,  # Normalize pixel values to [0,1]
        rotation_range=10,  # Rotate images randomly
        width_shift_range=0.1,  # Shift width randomly
        height_shift_range=0.1,  # Shift height randomly
        shear_range=0.1,  # Shear transformation
        zoom_range=0.1,  # Zoom in/out
        horizontal_flip=True,  # Flip images horizontally
        validation_split=0.2  # Split dataset into training (80%) and validation (20%)
    )

    # Load training data
    train_generator = datagen.flow_from_directory(
        dataset_path,
        target_size=img_size,
        color_mode="grayscale",
        batch_size=batch_size,
        class_mode="categorical",
        subset="training"
    )

    # Load validation data
    val_generator = datagen.flow_from_directory(
        dataset_path,
        target_size=img_size,
        color_mode="grayscale",
        batch_size=batch_size,
        class_mode="categorical",
        subset="validation"
    )

    # Define the CNN model
    model = Sequential([
        Conv2D(32, (3, 3), activation='relu', input_shape=(img_size[0], img_size[1], 1)),
        BatchNormalization(),
        MaxPooling2D(2, 2),

        Conv2D(64, (3, 3), activation='relu'),
        BatchNormalization(),
        MaxPooling2D(2, 2),

        Conv2D(128, (3, 3), activation='relu'),
        BatchNormalization(),
        MaxPooling2D(2, 2),

        Flatten(),
        Dense(128, activation='relu'),
        Dropout(0.5),
        Dense(len(train_generator.class_indices), activation='softmax')  # Output layer
    ])

    # Compile the model
    model.compile(
        optimizer=Adam(learning_rate=0.0005),
        loss="categorical_crossentropy",
        metrics=["accuracy"]
    )

    # Train the model
    history = model.fit(
        train_generator,
        validation_data=val_generator,
        epochs=epochs
    )

    # Save the trained model
    model.save("emotion_model.h5")

    return model, history

dataset_path = "emotion_dataset/train"  # Change to your dataset path
model, history = train_emotion_model(dataset_path, epochs=30)