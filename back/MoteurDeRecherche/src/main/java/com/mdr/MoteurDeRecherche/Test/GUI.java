package com.mdr.MoteurDeRecherche.Test;

import com.mdr.MoteurDeRecherche.Utils.Indexation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class GUI extends JTextArea{

    public static void main(String[] args) {

        //Creation d'une fenêtre
        JFrame frame = new JFrame("Logiciel pour la base de données");
        frame.setSize(400, 400);
        frame.setLocation(300,200);




        //Panel de bouton
        JPanel panel = new JPanel(); // the panel is not visible in output
        JTextArea jTextArea1 = new JTextArea(10, 40);


        //Creation des boutons
        final JButton buildBookButton = new JButton("BuildBooksDatabase");
        final JButton buildIndexBookButton = new JButton("Index Books");
        panel.add(buildBookButton); // Components Added using Flow Layout
        panel.add(buildIndexBookButton);

        frame.getContentPane().add(BorderLayout.SOUTH, panel);


        JTextArea textArea = new JTextArea(10, 40);
        frame.getContentPane().add(BorderLayout.CENTER, textArea);

        buildBookButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               int response = JOptionPane.showConfirmDialog(frame,
                        "Telecharger les livres de la base de données ? ", "CONFIRMATION", JOptionPane.YES_NO_OPTION);
               if(response==0){
                   textArea.append("Telechargement des livres..Veuillez Patientez\n");
               }


            }
        });

        buildIndexBookButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(frame,
                        "Indexer les livres de la base de données ? ", "CONFIRMATION", JOptionPane.YES_NO_OPTION);
                if(response==0){
                    textArea.append("Indexation en cours...Veuillez Patientez\n");
                    try {
                        //Indexation.indexBookDatabase();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    textArea.append("Indexation terminé ! \n");
                }

            }
        });

        /*final JTextArea textArea = new JTextArea(10, 40);
        f.getContentPane().add(BorderLayout.CENTER, textArea);
        final JButton button = new JButton("Click Me");
        f.getContentPane().add(BorderLayout.SOUTH, button);
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.append("Button was clicked\n");

            }
        });*/

        frame.setVisible(true);

    }



}
