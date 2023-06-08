from fuzzywuzzy import fuzz
from getting_ppa_value import get_ppa_value
import getting_date
from ultralytics import YOLO
import cv2
import numpy as np
import torch
import os
import shutil
from paddleocr import PaddleOCR
import concurrent.futures
import threading
directoryPath = "./file_with_segmentations"

ocr_model = PaddleOCR(lang='fr', use_angle_cls=True, show_log=False)

model = YOLO('best_ever.pt')
model_champs_NAME_DATE_DOS = YOLO('best_train6.pt')
model_champs_PPA_FORM = YOLO('best_train4.pt')


classes_names={'0':'Forme' , '1':'Dossage' , '2': 'PPA' , '3' : 'Date' , '4': 'Name'}

vignette_color ={'0':'Green' , '1':'White' , '2': 'Red'}

input_size = (640, 640)



def getting_class_ids_to_output(class_ids_NDD,class_ids_PF):
    set1 = set(class_ids_NDD.tolist())
    set2 = set(class_ids_PF.tolist())

    result1 = set2 - set1
    result_to_add_to_PF = list(result1.intersection({1, 3, 4}))
    result_to_output_from_NDD = list((set1- set(result1)).intersection({1, 3, 4}))

    result2 = set1 - set2
    result_to_add_to_NDD = list(result2.intersection({0,2}))
    result_to_output_from_PF = list((set2- set(result2)).intersection({0,2}))


    class_ids_to_output_from_PF = result_to_output_from_PF + result_to_add_to_PF
    class_ids_to_output_from_NDD = result_to_output_from_NDD + result_to_add_to_NDD

    return class_ids_to_output_from_NDD , class_ids_to_output_from_PF


def segmenting_and_saving(img,masks,class_ids ,class_ids_to_output_from_):
    # Create new image with transparent background

    new_ppa = np.zeros((img.shape[0], img.shape[1], 4), dtype=np.uint8)
    new_date = np.zeros((img.shape[0], img.shape[1], 4), dtype=np.uint8)


    # Create a copy of the original image
    img_without_date_ppa = img.copy()

    for mask, class_id in zip(masks, class_ids):
        if int(class_id.item()) in class_ids_to_output_from_ :
            class_name = classes_names[str(int(class_id.item()))]
            # print('i am outputing : '+str(class_name))


            # if class_name == 'Date' or class_name ==  'Name' or class_name == 'Dossage' :
            #  Check if class name is 'Date'
            if class_name == 'Date':

                # Convert mask to boolean mask
                bool_mask_date = mask.to(torch.bool).numpy()

                # Resize boolean mask to match img shape
                bool_mask_date = cv2.resize(bool_mask_date.astype(np.uint8), (img.shape[1], img.shape[0])).astype(bool)

                # Use boolean mask to index img array
                new_date[bool_mask_date] = img[bool_mask_date]

                # Set the pixels corresponding to the Date class to white in the copy of the original image
                img_without_date_ppa[bool_mask_date] =  [0, 0, 0 , 0]
                cv2.imwrite(f'./{directoryPath}/Date.png', new_date)
                # cv2.imwrite(f'./{directoryPath}/without_Date.jpg', img_without_date_ppa)
            if class_name == 'Forme':

                # Convert mask to boolean mask
                bool_mask_date = mask.to(torch.bool).numpy()

                # Resize boolean mask to match img shape
                bool_mask_date = cv2.resize(bool_mask_date.astype(np.uint8), (img.shape[1], img.shape[0])).astype(bool)

                # Use boolean mask to index img array
                new_date[bool_mask_date] = img[bool_mask_date]

                # Set the pixels corresponding to the Date class to white in the copy of the original image
                img_without_date_ppa[bool_mask_date] =  [0, 0, 0 , 0]
                cv2.imwrite(f'./{directoryPath}/Forme.png', new_date)
                # cv2.imwrite(f'./{directoryPath}/without_Date.jpg', img_without_date_ppa)



            if class_name == 'PPA':
                # Convert mask to boolean mask
                bool_mask_ppa = mask.to(torch.bool).numpy()

                # Resize boolean mask to match img shape
                bool_mask_ppa = cv2.resize(bool_mask_ppa.astype(np.uint8), (img.shape[1], img.shape[0])).astype(bool)

                # Use boolean mask to index img array
                new_ppa[bool_mask_ppa] = img[bool_mask_ppa]

                # Set the pixels corresponding to the Date class to white in the copy of the original image
                img_without_date_ppa[bool_mask_ppa] = [0, 0, 0 , 0]
                cv2.imwrite(f'./{directoryPath}/PPA.png', new_ppa)
                # cv2.imwrite(f'./{directoryPath}/without_PPA.jpg', img_without_date_ppa)
    return img_without_date_ppa




    # Save new image with class name in file name


    # Save the copy of the original image without the Date class
    # cv2.imwrite(f'./{directoryPath}/without_Date_PPA.jpg', img_without_date_ppa)




def drop_duplicants(class_tensor,conf_tensor):
    unique_elements, counts = torch.unique(class_tensor, return_counts=True)

    # Find the elements that appear more than once
    repeated_elements = unique_elements[counts > 1]

    # Find the positions of the repeated elements
    positions = [torch.where(class_tensor == element) for element in repeated_elements]
    positions_to_del=[]
    for pos in positions:
        positions_to_del.append(pos[0].tolist())

    new_pos = np.array(range(0,len(class_tensor )))

    for t in positions_to_del :
        for j in t :
            new_pos = np.delete(new_pos,np.where(new_pos==j))


    pos_to_keep = []
    for t in positions_to_del :
            pos_to_keep.append(conf_tensor.tolist().index(max(conf_tensor[t].tolist())))

    new_pos= np.append(new_pos,pos_to_keep)

    return new_pos


def  get_product_id_from_segmented_image(path,vig_color_id):
    id = 0
    try:
        result = ocr_model.ocr("./file_with_segmentations/without_Date_PPA.png", cls=True)

        all_text = " ".join([res[1][0] for res in result[0]])
        id = fuzzy_best_match_id(all_text,vig_color_id)
    except:
        pass
    return int(id)



def fuzzy_best_match_id(text , vig_class_id):


    meds_file_path = ""
    if vig_class_id == 0:
        meds_file_path = "green_meds.txt"
    elif vig_class_id == 2:
        meds_file_path = "red_meds.TXT"

    best_match_ratio = 0
    best_match_id = None

    with open(meds_file_path, 'r') as file:
        for line in file:
            line = line.strip()
            parts = line.split('###')
            if len(parts) != 2:
                continue

            current_id = parts[0]
            current_text = parts[1]

            ratio = fuzz.token_sort_ratio(text, current_text)

            if ratio > best_match_ratio:
                best_match_ratio = ratio
                best_match_id = current_id

    #print("best match id in the function is {} ".format(best_match_id))
    return best_match_id





#BEGINS HERE

def get_product_id_date_ppa(src):


    img = cv2.imread(src)



    # Python program to check
    # if a directory contains file




    # path of the directory

    # Check whether the specified
    # path exists or not
    isExist = os.path.exists(directoryPath)
    if not isExist :
            os.mkdir(directoryPath)

    # Comparing the returned list to empty list
    if os.listdir(directoryPath) == []:
              #print("No files found in the directory.")
              pass
    else:
        shutil.rmtree(directoryPath)
        os.mkdir(directoryPath)


    # Pass the frame to the YOLO model for prediction
    results = model.predict(source=src,conf=0.7,show=False, save_crop=False , save = False,retina_masks=True , verbose=False)

    #THE CODITION IS FOR IF HE IS DETECTING A VIGNETTE
    if (len(results[0].boxes.cls)>0):
        color_id  =results[0].boxes.cls[0]
        #THE CODITION IS FOR IF HE IS DETECTING A Green or Red VIGNETTE

        if (color_id==0) or (color_id==2):
            #color contains Green or Red
            color = vignette_color[str(int(color_id))]

            #print(color)
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

                    def run_model_champs_NAME_DATE_DOS():
                        global results_NDD
                        results_NDD = model_champs_NAME_DATE_DOS.predict(source=output_path, show=False, save=False,
                                                                         show_labels=False, show_conf=False, conf=0.1,
                                                                         save_txt=False, save_crop=False,
                                                                         retina_masks=True,
                                                                         save_conf=True, verbose=False)

                    def run_model_champs_PPA_FORM():
                        global results_PF
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

                    #DELETING GENERATED SEGMENTED IMAGE OF THE VIGNETTE, bcs WE DONT NEED IT





                    conf_tensor_NDD  = results_NDD[0].boxes.conf
                    class_tensor_NDD = results_NDD[0].boxes.cls


                    conf_tensor_PF  = results_PF[0].boxes.conf
                    class_tensor_PF = results_PF[0].boxes.cls
                    # Assuming you have a torch tensor `x`


                    new_pos_NDD = drop_duplicants(class_tensor_NDD,conf_tensor_NDD)

                    new_pos_PF = drop_duplicants(class_tensor_PF,conf_tensor_PF)

                    cls_NDD=results_NDD[0].boxes.cls[new_pos_NDD]
                    data_NDD = results_NDD[0].masks.data[new_pos_NDD]

                    cls_PF=results_PF[0].boxes.cls[new_pos_PF]
                    data_PF = results_PF[0].masks.data[new_pos_PF]




                    #THE CONDITION BELOW IS OPTIONAL
                    # results[0].boxes.cls == 4 TO CHECK THAT THE NAME HAS BEEN DETECTED , len(results[0].boxes.cls)>=3 TO CHECK IF AT LEAST 3 CHAMPS HAVE BEEN DETECTED

                    if (torch.any(results_NDD[0].boxes.cls == 4)) and (len(results_NDD[0].boxes.cls)>=3) :
                        #print("I HAVE CHAMPS")

                        output_dir = f'./file_with_segmentations'
                        # os.mkdir(output_dir)


                        masks_NDD = data_NDD
                        masks_PF = data_PF


                        # Get class names of detected objects
                        class_ids_NDD =  cls_NDD
                        class_ids_PF = cls_PF


                        class_ids_to_output_from_NDD ,class_ids_to_output_from_PF = getting_class_ids_to_output(class_ids_NDD,class_ids_PF)

                        # print(class_ids_to_output_from_NDD)
                        # print(class_ids_to_output_from_PF)
                            # Iterate over segmentation masks and class names
                        img = new_img


                        img_without_date_ppa = segmenting_and_saving(img,masks_NDD,class_ids_NDD ,class_ids_to_output_from_NDD)
                        img_without_date_ppa = segmenting_and_saving(img_without_date_ppa,masks_PF,class_ids_PF ,class_ids_to_output_from_PF)

                        cv2.imwrite(f'./{directoryPath}/without_Date_PPA.png', img_without_date_ppa)



                        location = "./"
                        path = os.path.join(location, output_path)
                        os.remove(path)

                    else:
                        pass
                        #print('I didn't catch it the name or i didnt catch at lesat 3 champs  , pls retry !')
        else:
            pass
            #print(' DETECTED A WHITE VIGNETTE')




    id = get_product_id_from_segmented_image(f'./{directoryPath}/without_Date_PPA.png',results[0].boxes.cls[0])

    ppa = get_ppa_value()

    lot,ddp = getting_date.get_lot_date()

    return id,ppa,lot,ddp

