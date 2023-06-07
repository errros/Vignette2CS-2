from paddleocr import PaddleOCR
from fuzzywuzzy import fuzz


text = "06K160/235 VIGNETTE SIOGALENIC DIOVEINE 600mg Dlosmlno600mg Bote de 30Cps pelluls"


def fuzzy_best_match_id(text , vig_class_id):


    meds_file_path = ""
    if vig_class_id == 0:
        meds_file_path = "green_meds.txt"
    elif vig_class_id == 2:
        meds_file_path = "red_meds.TXT"

    print("lfile howa {}".format(meds_file_path))
    best_match_ratio = 0
    best_match_id = None

    with open(meds_file_path, 'r') as file:
        for line in file:

            line = line.strip()
            parts = line.split('###')
            if len(parts) != 2:
                continue

            print("len rah 2 ta3 parts {}".format(meds_file_path))

            current_id = parts[0]
            current_text = parts[1]

            ratio = fuzz.token_sort_ratio(text, current_text)

            if ratio > best_match_ratio:
                best_match_ratio = ratio
                best_match_id = current_id

    print("best match id in the function is {} ".format(best_match_id))
    return best_match_id
fuzzy_best_match_id(text,0)