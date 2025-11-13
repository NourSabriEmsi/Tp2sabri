package ma.emsi.nour.tp2sabri.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.emsi.nour.tp2sabri.llm.LlmClientPourGemini;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class Bb implements Serializable {
    private String roleSysteme;
    private boolean roleSystemeChangeable = true;
    private List<SelectItem> listeRolesSysteme;
    private String question;
    private String reponse;
    private StringBuilder conversation = new StringBuilder();
    @Inject
    private FacesContext facesContext;
    @Inject
    private LlmClientPourGemini llmClientPourGemini;
    public Bb() {
    }
    public String getRoleSysteme() {
        return roleSysteme;
    }
    public void setRoleSysteme(String roleSysteme) {
        this.roleSysteme = roleSysteme;
    }
    public boolean isRoleSystemeChangeable() {
        return roleSystemeChangeable;
    }
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public String getReponse() {
        return reponse;
    }
    public void setReponse(String reponse) {
        this.reponse = reponse;
    }
    public String getConversation() {
        return conversation.toString();
    }
    public void setConversation(String conversation) {
        this.conversation = new StringBuilder(conversation);
    }
    public String envoyer() {
        if (question == null || question.isBlank()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Texte question vide", "Il manque le texte de la question");
            facesContext.addMessage(null, message);
            return null;
        }
        try {
            // Si première question, on précise le rôle système
            if (roleSystemeChangeable) {
                llmClientPourGemini.setSystemRole(roleSysteme);
                roleSystemeChangeable = false;
            }
            reponse = llmClientPourGemini.envoyerRequete(question);
            afficherConversation();
            afficherConversation();
        } catch (Exception e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Problème de connexion avec l'API du LLM", e.getMessage());
            facesContext.addMessage(null, message);
        }
        return null;
    }
    public String nouveauChat() {
        return "index";
    }
    private void afficherConversation() {
        this.conversation.append("* User:\n").append(question).append("\n* GPT:\n").append(reponse).append("\n");
    }
    public List<SelectItem> getRolesSysteme() {
        if (this.listeRolesSysteme == null) {
            this.listeRolesSysteme = new ArrayList<>();
            String role = """
                    You are a helpful assistant. You help the user to find the information they need.
                    If the user type a question, you answer it.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Assistant"));
            role = """
                    You are an interpreter. You translate from English to French and from French to English.
                    If the user type a French text, you translate it into English.
                    If the user type an English text, you translate it into French.
                    If the text contains only one to three words, give some examples of usage of these words in English.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Traducteur Anglais-Français"));
            role = """
                    Your are a travel guide. If the user type the name of a country or of a town,
                    you tell them what are the main places to visit in the country or the town
                    are you tell them the average price of a meal.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Guide touristique"));
            role = """
                    Coach motivationnel : Réponds toujours de manière motivante et encourageante.
                    Encourage l’utilisateur, donne des conseils pratiques et inspire-le à persévérer.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Coach motivationnel"));
        }
        return this.listeRolesSysteme;
    }
}