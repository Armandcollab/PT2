package evenements;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Generateur {

    static Date aujourdhui;
    static String dossierTemplates;
    static String dossierSortie;
    static String adresseSiteWeb;
    static HashMap<String, String> valeursMotsCles;

    /**
     * Générer le site web
     */
    static void generer() {
        // Date d'aujourd'hui
        aujourdhui = new Date();

        // Peut être surchargée en utilisant la variable d'environement
        if (System.getenv("DATE_GENERATION") != null) {
            aujourdhui = Seance.chaineVersDate(System.getenv("DATE_GENERATION"));
        }

        valeursMotsCles = new HashMap<>();
        dossierTemplates = System.getenv("TEMPLATES_DIRECTORY");
        dossierSortie = System.getenv("WEBSITE_DIRECTORY");
        adresseSiteWeb = System.getenv("WEBSITE_URL");

        genererPlan();
        // genererPagesSalles();
        // genererPageEvenements();
    }

    /**
     * Traite un template en utilisant les valeurs des mots clés définis
     */
    static void traiterTemplate(String entree, String sortie) {
        FormatHtml.traiterTemplate(dossierTemplates + "/" + entree, dossierSortie
                + "/" + sortie, valeursMotsCles);
    }

    /**
     * Traite un template (même nom en entrée et sortie)
     */
    static void traiterTemplate(String page) {
        traiterTemplate(page, page);
    }

    /**
     * Retourne le code HTML qui affiche les salles
     */
    static String listeSallesHtml(String evenement) {
        if (evenement == null) {
            String sallesHtml = "<ul>";

            for (String e : BaseDeDonnees.obtenirSallesParEtages().keySet()) {
                sallesHtml += "<li>" + e + "</li>";
                sallesHtml += "<ul>";
                for (Salle s : BaseDeDonnees.obtenirSallesParEtages().get(e)) {
                    sallesHtml += "<li>" + s.nom + "</li>";
                }
                sallesHtml += "</ul>";
            }

            sallesHtml += "</ul>";

            return sallesHtml;
        } else {
            String planHtml = "<div class=\"etage\">";
            for (String e : BaseDeDonnees.obtenirSallesParEtages().keySet()) {
                planHtml += "<div class=\"etage_" + e + "\">";
                planHtml += "<p>Etage : " + e + "</p>";
                planHtml += "<img src=\"imgs/plan/" + e + ".png\" alt=\"" + e + "\" style=\" height:auto; width:;\">";
                for (Salle s : BaseDeDonnees.obtenirSallesParEtages().get(e)) {
                    int y = s.Yhautgauche / 6;
                    int x = s.Xhautgauche / 6;
                    int l = s.largeur / 6;
                    int h = s.hauteur / 6;
                    planHtml += "<a href=\"salle-" + "[[SALLE]]" + ".html\" style=\"top:"
                            + y + "px; left: " + x + "px; height: "
                            + h + "px; width: "
                            + l + "px;\">" + s.nom + "</a>";
                }
                planHtml += "</div>";
            }
            planHtml += "</div>";
            return planHtml;
        }
    }

    /**
     * Génère la page du plan
     */
    static void genererPlan() {
        valeursMotsCles.put("[[SALLES]]", listeSallesHtml("truc"));
        traiterTemplate("plan.html");
    }

    /**
     * Créé le code HTML d'une liste de séances
     */
    static String seancesVersListeHtml(ArrayList<Seance> seances) {
        throw new UnsupportedOperationException("A implémenter");
    }

    /**
     * Génère le code HTML d'une liste de séances formatées pour l'affichage
     * esthétique
     */
    static String seancesVersHtmlFormate(ArrayList<Seance> seances) {
        String seance_Html = "";
        for (Seance s : seances) {
            String[] tab_Deb = Seance.dateVersHeure(s.dateDebut).split(":");
            String[] tab_Fin = Seance.dateVersHeure(s.dateFin).split(":");
            seance_Html += "<div class=\"salle\">";
            seance_Html += "<div class=\"nav_date\">";
            seance_Html += "<p>" + "date" + "</p>"; // completer avec la récupération le la date actuelle
            seance_Html += "<a href=\"salle-" + "[[SALLE]]" + "-" + "" + ".html\"><imgs/right.png alt=\"rght\"</a>"; // accès à la page du jour suivant
            seance_Html += "<a href=\"salle-" + "[[SALLE]]" + ".html\" id=\"auj\">aujourd'hui</a>"; // retour à la page courante de la salle
            seance_Html += "</div>"; // fin class nav_salle
            seance_Html += "<div class=\"planning\">";
            int top = (8 - Integer.parseInt(tab_Deb[0])) * 10 
                    + ((60 - Integer.parseInt(tab_Deb[1]) / 10)) + (8 - Integer.parseInt(tab_Deb[0]));  //  placement séance en % avec 
            int height = (Integer.parseInt(tab_Fin[0]) - Integer.parseInt(tab_Deb[0])) * 10
                    + 10 + ((Integer.parseInt(tab_Deb[1]) - Integer.parseInt(tab_Fin[2])) / 10); // taille de la séance en %
            seance_Html += "<div class=\"seance\" style=\"top:" + top + "; height:" + height + ";\">"; // dessin des séances
            seance_Html += "<h2>" + s.titre + "</h2>"; // ajouter le titre de la séance
            seance_Html += "<div class=\"description\">";
            seance_Html += "<p>" + s.description + "</p>"; // description de la séance import de la base
            seance_Html += "<p>" + s.type + "</p>";
            seance_Html += "<p>" + s.promotion + "</p>"; // promotion de la séance import de la base
            seance_Html += "</div>"; // fin class description
            // ajout de l'horaire de la séance A faire: convertion de la date en Heure
            seance_Html += "<p>" + Seance.dateVersHeure(s.dateDebut)
                    + "-->" + Seance.dateVersHeure(s.dateFin) + "</p>";
            seance_Html += "</div>"; // fin class seance
            seance_Html += "<div class=\"horaire\">";
            for (int i = 8; i <= 17; i++) {
                seance_Html += "<div class=\"echelle\">";
                seance_Html += "<p>" + i + ":00 </p>";
                seance_Html += "<hr>";
                seance_Html += "</div>"; // fin class echelle
            }
            seance_Html += "</div>"; // fin class horaire
            seance_Html += "</div>"; // fin class planning
            seance_Html += "</div>";
        }
        return seance_Html;
    }

    /**
     * Génère les pages des salles
     */
    static void genererPagesSalles() {
        throw new UnsupportedOperationException("A implémenter");
    }

    /**
     * Génère la page des événements et chaque page d'événement
     */
    static void genererPageEvenements() {
        throw new UnsupportedOperationException("A implémenter");
    }

    /**
     * Génère la page d'un événement
     */
    static void genererPageEvenement(Evenement evenement) {
        throw new UnsupportedOperationException("A implémenter");
    }
}
