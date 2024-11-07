package com.tudu.tu_du.models;

public class Mensajes {
   private String id;
   private String idEmisor;
   private String idReceptor;
   private String idChat;
   private String mensaje;
   private long timestamp;
   private boolean visto;


   public Mensajes() {

   }


    public Mensajes(String id, String idEmisor, String idReceptor, String idChat, String mensaje, long timestamp, boolean visto) {
        this.id = id;
        this.idEmisor = idEmisor;
        this.idReceptor = idReceptor;
        this.idChat = idChat;
        this.mensaje = mensaje;
        this.timestamp = timestamp;
        this.visto = visto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdEmisor() {
        return idEmisor;
    }

    public void setIdEmisor(String idEmisor) {
        this.idEmisor = idEmisor;
    }

    public String getIdReceptor() {
        return idReceptor;
    }

    public void setIdReceptor(String idReceptor) {
        this.idReceptor = idReceptor;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }
}
