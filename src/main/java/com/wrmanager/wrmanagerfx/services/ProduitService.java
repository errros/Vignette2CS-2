package com.wrmanager.wrmanagerfx.services;

import com.wrmanager.wrmanagerfx.controllers.ProduitsViewController;
import com.wrmanager.wrmanagerfx.entities.Categorie;
import com.wrmanager.wrmanagerfx.entities.Produit;
import com.wrmanager.wrmanagerfx.models.SystemMeasure;
import de.jonato.jfxc.controls.barcode.core.BarcodeEncoding;
import de.jonato.jfxc.controls.barcode.core.BarcodeFX;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uk.org.okapibarcode.backend.Ean;
import uk.org.okapibarcode.backend.Symbol;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static com.wrmanager.wrmanagerfx.Constants.*;
public class ProduitService {


    public final String filePath = "src/main/api/red_meds.txt";

    public String generateEAN13(){
        String s = String.format("%040d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16)).substring(0,12);

        return s.concat(String.valueOf(checkSum(s)));


    }

    private char checkSum(String s ){

        int count = 0;
        int p = 0;

        for (int i = s.length() - 1; i >= 0; i--) {
            int c = Character.getNumericValue(s.charAt(i));
            if (p % 2 == 0) {
                c = c * 3;
            }
            count += c;
            p++;
        }

        int cdigit = 10 - (count % 10);
        if (cdigit == 10) {
            cdigit = 0;
        }

return         (char) (cdigit + '0');
    }

    private void updateFileInsert(Produit produit) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // Append the new data to the file
            String newData = produit.getId() + "###" + produit.getDesignation().toUpperCase() + " " + produit.getDosage().toUpperCase();
            writer.append(newData);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception accordingly
        }
    }


    private void updateFile( Produit produit) {

        try {
            File file = new File(filePath);

            // Read the contents of the file
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));

            // Find the line containing produit.getId()
            String lineToReplace = produit.getId() + "###";
            int startIndex = content.indexOf(lineToReplace);
            int endIndex = content.indexOf(System.lineSeparator(), startIndex);
            if (endIndex == -1) {
                endIndex = content.length();
            }

            // Update the line with ret.designation###ret.dosage
            String updatedLine = produit.getId() + "###"+  produit.getDesignation().toUpperCase() + " " + produit.getDosage().toUpperCase();
            String updatedContent = content.substring(0, startIndex) + updatedLine + content.substring(endIndex);

            // Write the updated content back to the file
            Files.write(Paths.get(file.toURI()), updatedContent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception accordingly
        }


    }



    public void deleteLineById(String id) {
        try {
            File file = new File(filePath);
            File tempFile = new File(file.getAbsolutePath() + ".tmp");

            // Create a temporary file
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            // Read the contents of the file
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String currentLine;

            // Copy lines to the temporary file, excluding the line with the specified id
            while ((currentLine = reader.readLine()) != null) {
                if (!currentLine.startsWith(id + "###")) {
                    writer.write(currentLine);
                    writer.newLine();
                }
            }

            // Close the readers and writers
            reader.close();
            writer.close();

            // Replace the original file with the temporary file
            if (file.delete()) {
                tempFile.renameTo(file);
            } else {
                throw new IOException("Failed to delete the original file: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception accordingly
        }
    }


    public Produit save (Produit produit , Categorie categorie){

        if(categorie.getId().equals(100l)) {
            produit.addCategorie(categorie);

            var ret = produitDAO.save(produit);
            produitsList.add(ret);
            sortRowsByLastTimeAdded();
            updateFileInsert(ret);

            return ret;
        }
        return null;



     }


     //test
     public Produit update (Produit produit , Categorie categorie){

      if(categorie.getId() == 100l) {
          if (!produit.getCategorie().equals(categorie)) {
              produit.removeCategorie(produit.getCategorie());
              produit.addCategorie(categorie);
          }

          var ret = produitDAO.update(produit);



          produitsViewController.getProduitsTable().refresh();
          stockViewController.getStocksTable().refresh();

          updateFile(ret);

          return ret;

      }
      return null;
     }



     public void sortRowsByLastTimeAdded(){

        produitsList.sort(Comparator.comparing(Produit::getCreeLe).reversed());

    }

     public void delete(Produit produit){

        if(produit.getCategorie().getId() == 100l) {
            deleteLineById(produit.getId().toString());

            produitDAO.deleteById(produit.getId());
            produitsList.remove(produit);
            sortRowsByLastTimeAdded();
        }

     }

     public List<Produit> getAll(){


        var list = produitDAO.getAll().stream().toList();
        return list;

     }









// Excel format:    CODEBAR , DESGINATION , PEERISABLE , SYSTEM UNITE , QTYUnite
/*

    public void saveExcelSheet(File file) throws IOException, InvalidFormatException {

        XSSFWorkbook workbook = new XSSFWorkbook(file);
        // Read student data form excel file sheet.
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {

            if (index > 0) {
                XSSFRow row = worksheet.getRow(index);
                Produit produit = Produit.builder().codeBarre(getCellValue(row, 0)).designation(getCellValue(row, 1)).
                        estPerissable(Boolean.valueOf(getCellValue(row, 2))).
                        systemMeasure(getCellValue(row,3) == SystemMeasure.UNITE.toString() ? SystemMeasure.UNITE : SystemMeasure.POIDS ).
                        qtyUnite(Float.valueOf(getCellValue(row,4))).build();

                //  students.add(student);
                save(produit,categorieDAO.getById(1l).get());
            }
        }

    }
*/

    private String getCellValue(Row row, int cellNo) {
        DataFormatter formatter = new DataFormatter();
        Cell cell = row.getCell(cellNo);
        return formatter.formatCellValue(cell);

    }




}
