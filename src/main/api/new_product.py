from ast import arguments
from ultralytics import YOLO
import cv2
import numpy as np
import torch
import os
import shutil
import sys
import multiprocessing


import os
from paddleocr import PaddleOCR, draw_ocr  # main OCR dependencies
from matplotlib import pyplot as plt  # plot images
import cv2  # opencv
import os  # folder directory navigation
import concurrent.futures
from paddleocr import PaddleOCR
import threading
import time
from concurrent.futures import ThreadPoolExecutor

start = time.time()

# path of the directory
directoryPath = "./file_with_segmentations"

ocr_model = PaddleOCR(lang='fr', use_angle_cls=True, show_log=False)
model = YOLO('./best_ever.pt')
model_champs_NAME_DATE_DOS = YOLO('./best_train6.pt')
model_champs_PPA_FORM = YOLO('./best_train4.pt')

classes_names = {'0': 'Forme', '1': 'Dossage', '2': 'PPA', '3': 'Date', '4': 'Name'}

input_size = (640, 640)


def getting_class_ids_to_output(class_ids_NDD, class_ids_PF):
    set1 = set(class_ids_NDD.tolist())
    set2 = set(class_ids_PF.tolist())

    result1 = set2 - set1
    result_to_add_to_PF = list(result1.intersection({1, 3, 4}))
    result_to_output_from_NDD = list((set1 - set(result1)).intersection({1, 3, 4}))

    result2 = set1 - set2
    result_to_add_to_NDD = list(result2.intersection({0, 2}))
    result_to_output_from_PF = list((set2 - set(result2)).intersection({0, 2}))

    class_ids_to_output_from_PF = result_to_output_from_PF + result_to_add_to_PF
    class_ids_to_output_from_NDD = result_to_output_from_NDD + result_to_add_to_NDD

    return class_ids_to_output_from_NDD, class_ids_to_output_from_PF


def segmenting_and_saving(img, masks, class_ids, class_ids_to_output_from_, output_dir=directoryPath):
    def process_segment(mask, class_id):
        if int(class_id.item()) in class_ids_to_output_from_:
            class_name = classes_names[str(int(class_id.item()))]

            # CONDITION HERE !!!!!!
            if class_name == 'Name' or class_name == 'Forme' or class_name == 'Dossage':

                new_img = np.zeros((img.shape[0], img.shape[1], 4), dtype=np.uint8)
                bool_mask = mask.to(torch.bool).numpy()
                bool_mask = cv2.resize(bool_mask.astype(np.uint8), (img.shape[1], img.shape[0])).astype(bool)
                new_img[bool_mask] = img[bool_mask]
                check_file = os.path.exists(f'{output_dir}/{class_name}.jpg')
                if check_file:
                    cv2.imwrite(f'{output_dir}/{class_name}_1.jpg', new_img)
                else:
                    cv2.imwrite(f'{output_dir}/{class_name}.jpg', new_img)

    with concurrent.futures.ThreadPoolExecutor() as executor:
        futures = [executor.submit(process_segment, mask, class_id) for mask, class_id in zip(masks, class_ids)]
        concurrent.futures.wait(futures)


def drop_duplicants(class_tensor, conf_tensor):
    dup_start = time.time()
    unique_elements, counts = torch.unique(class_tensor, return_counts=True)

    # Find the elements that appear more than once
    repeated_elements = unique_elements[counts > 1]

    # Find the positions of the repeated elements
    positions = [torch.where(class_tensor == element) for element in repeated_elements]
    positions_to_del = []
    for pos in positions:
        positions_to_del.append(pos[0].tolist())

    new_pos = np.array(range(0, len(class_tensor)))

    for t in positions_to_del:
        for j in t:
            new_pos = np.delete(new_pos, np.where(new_pos == j))

    pos_to_keep = []
    for t in positions_to_del:
        pos_to_keep.append(conf_tensor.tolist().index(max(conf_tensor[t].tolist())))

    new_pos = np.append(new_pos, pos_to_keep)

    dup_end = time.time()

    return new_pos



# BEGINS HERE




def new_product(src):
    # Check whether the specified
    # path exists or not
    isExist = os.path.exists(directoryPath)
    if not isExist:
        os.mkdir(directoryPath)

    # Comparing the returned list to empty list
    if os.listdir(directoryPath) == []:
        # print("No files found in the directory.")
        pass
    else:
        shutil.rmtree(directoryPath)
        os.mkdir(directoryPath)

    img = cv2.imread(src)

    # Pass the frame to the YOLO model for prediction
    results = model.predict(source=src, conf=0.7, show=False, save_crop=False, save=False, retina_masks=True, verbose=False)

    loop_start = time.time()
    # THE CODITION IS FOR IF HE IS DETECTING A VIGNETTE
    if (len(results[0].boxes.cls) > 0):
        # TO check if vignette is red !
        if (results[0].boxes.cls[0] == 2):

            for r in results:

                img = cv2.cvtColor(img, cv2.COLOR_BGR2RGBA)
                # Get segmentation masks
                masks = r.masks.data

                # Create new image with transparent background
                new_img = np.zeros((img.shape[0], img.shape[1], 4), dtype=np.uint8)
                for mask in masks:
                    # Convert mask to boolean mask
                    bool_mask = mask.to(torch.bool).numpy()

                    # Resize boolean mask to match img shape
                    bool_mask = cv2.resize(bool_mask.astype(np.uint8), (img.shape[1], img.shape[0])).astype(bool)

                    # Use boolean mask to index img array
                    new_img[bool_mask] = img[bool_mask]
                    # Save new image
                    output_path = f'./{src}_segmented.png'
                    cv2.imwrite(output_path, new_img)
                    m2all_start = time.time()


                    def run_model_champs_NAME_DATE_DOS():
                        global results_NDD
                        results_NDD = model_champs_NAME_DATE_DOS.predict(source=output_path, show=False, save=False,
                                                                         show_labels=False, show_conf=False, conf=0.1,
                                                                         save_txt=False, save_crop=False, retina_masks=True,
                                                                         save_conf=True, verbose=False)


                    def run_model_champs_PPA_FORM():
                        global results_PF
                        m22_start = time.time()
                        results_PF = model_champs_PPA_FORM.predict(source=output_path, show=False, save=False,
                                                                   show_labels=False, show_conf=False, conf=0.1,
                                                                   save_txt=False, save_crop=False, retina_masks=True,
                                                                   save_conf=True, verbose=False)


                    # Create threads
                    thread_NDD = threading.Thread(target=run_model_champs_NAME_DATE_DOS)
                    thread_PF = threading.Thread(target=run_model_champs_PPA_FORM)

                    # Start threads
                    thread_NDD.start()
                    thread_PF.start()

                    # Wait for threads to complete
                    thread_NDD.join()
                    thread_PF.join()

                    # DELETING GENERATED SEGMENTED IMAGE OF THE VIGNETTE, bcs WE DONT NEED IT

                    location = "./"
                    path = os.path.join(location, output_path)
                    os.remove(path)

                    conf_tensor_NDD = results_NDD[0].boxes.conf
                    class_tensor_NDD = results_NDD[0].boxes.cls
                    # print(conf_tensor_NDD)
                    # print(class_tensor_NDD)

                    conf_tensor_PF = results_PF[0].boxes.conf
                    class_tensor_PF = results_PF[0].boxes.cls
                    # Assuming you have a torch tensor `x`

                    new_pos_NDD = drop_duplicants(class_tensor_NDD, conf_tensor_NDD)

                    new_pos_PF = drop_duplicants(class_tensor_PF, conf_tensor_PF)

                    cls_NDD = results_NDD[0].boxes.cls[new_pos_NDD]
                    data_NDD = results_NDD[0].masks.data[new_pos_NDD]

                    cls_PF = results_PF[0].boxes.cls[new_pos_PF]
                    data_PF = results_PF[0].masks.data[new_pos_PF]

                    # THE CONDITION BELOW IS OPTIONAL
                    # results[0].boxes.cls == 4 TO CHECK THAT THE NAME HAS BEEN DETECTED , len(results[0].boxes.cls)>=3 TO CHECK IF AT LEAST 3 CHAMPS HAVE BEEN DETECTED

                    if (torch.any(results_NDD[0].boxes.cls == 4)) and (len(results_NDD[0].boxes.cls) >= 3):
                        # print("I HAVE CHAMPS")

                        output_dir = f'./file_with_segmentations'
                        # os.mkdir(output_dir)

                        masks_NDD = data_NDD
                        masks_PF = data_PF

                        # Get class names of detected objects
                        class_ids_NDD = cls_NDD
                        class_ids_PF = cls_PF

                        class_ids_to_output_from_NDD, class_ids_to_output_from_PF = getting_class_ids_to_output(
                            class_ids_NDD,
                            class_ids_PF)

                        # print(class_ids_to_output_from_NDD)
                        # print(class_ids_to_output_from_PF)
                        # Iterate over segmentation masks and class names

                        segmenting_and_saving(img, masks_NDD, class_ids_NDD, class_ids_to_output_from_NDD)
                        segmenting_and_saving(img, masks_PF, class_ids_PF, class_ids_to_output_from_PF)


                    else:
                        # print('I didnt catch it the name or i didnt catch at lesat 3 champs  , pls retry !')
                        pass
            else:
                # print('it must be a red vignette to add new product')
                pass

    paths = ["./file_with_segmentations/Name.jpg", "./file_with_segmentations/Dossage.jpg",
             "./file_with_segmentations/Forme.jpg"]


    name = ""
    dos=""
    form=""
    try:
        result = ocr_model.ocr(paths[0], cls=True)
        name = "".join([res[1][0] for res in result[0]])
    except:

        pass
    try:
        result = ocr_model.ocr(paths[1], cls=True)
        dos = "".join([res[1][0] for res in result[0]])
    except:
        pass

    try:
        result = ocr_model.ocr(paths[2], cls=True)
        form = "".join([res[1][0] for res in result[0]])
    except:
        pass

    return name,dos,form

