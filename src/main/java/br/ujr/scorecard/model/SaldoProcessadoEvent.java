package br.ujr.scorecard.model;

public class SaldoProcessadoEvent {
	
	private Event  event;
	private String message;
	private Object payLoad;
	
	public enum Event {
		INICIADO, MENSAGEM, FINALIZADO;
	}
	
	public SaldoProcessadoEvent(Event event, String message) {
		this.event = event;
		this.message = message;
	}
	
	public SaldoProcessadoEvent(String message, Object refIni) {
		this.event = Event.MENSAGEM;
		this.message = message;
		this.payLoad = refIni;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getPayLoad() {
		return payLoad;
	}

	public void setPayLoad(Object payLoad) {
		this.payLoad = payLoad;
	}

}
