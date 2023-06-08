# -*- coding: utf-8 -*-
"""
Created on Wed Jun  7 19:04:52 2023

@author: jm_kh
"""


import mysql.connector
import random
from datetime import datetime

# Connect to the MySQL database
connection = mysql.connector.connect(
    host='localhost',
    user='root',
    password='15239',
    database='wrmanager'
)
cursor = connection.cursor()

with open('green_meds.txt', 'r') as file1, open('Medicaments.txt', 'r') as file2:
    for line1, line2 in zip(file1, file2):
        values1 = line1.strip().split('###')
        id_value = int(values1[0])
        print("id", id_value)
        codeBarre_value = str(random.randint(10 ** 12, 10 ** 13 - 1))
        creeLe_value = datetime.now().strftime('%Y-%m-%d %H:%M:%S.%f')
        print("codeabarre", codeBarre_value)
        print("creele", creeLe_value)
        values2 = line2.strip().split('#')
        designation_value = values2[1]
        print("designation", designation_value)
        dosage_value = values2[3]
        print("dosage", dosage_value)
        # Extract the third_file_value
        third_file_value = str(values2[13])
        forme_value = ''
        # Read the third file to extract forme
        with open('forme.txt', 'r') as file3:  # Replace with the name of the third file
            for line3 in file3:
                values3 = line3.strip().split(' ')
                first_forme = values3[0]
                if first_forme.startswith(third_file_value):
                    forme_value = first_forme[3:]
                    print("forme_value", forme_value)
                    print("**********************************")
        # Insert the values into the table
        query = """
            INSERT INTO produit (id, codeBarre, creeLe, designation, dosage, forme, categorie_id)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
        """
        values = (id_value, codeBarre_value, creeLe_value, designation_value, dosage_value, forme_value, 99)
        cursor.execute(query, values)

# Commit the changes and close the connection
connection.commit()
cursor.close()
connection.close()
