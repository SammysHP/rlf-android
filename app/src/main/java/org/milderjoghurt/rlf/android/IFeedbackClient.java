package org.milderjoghurt.rlf.android;

import org.milderjoghurt.rlf.android.dummy.Session;

import java.util.List;

public interface IFeedbackClient {
    /**
     * Gibt eine Übersicht aller Sitzungen Zurück
     *
     * @return Liste der Sitzung
     */
    List<Session> getSessions();

    /**
     * Läd eine Sitzung vom Server und gibt diese Zurück
     *
     * @param SessionId
     * @return Die Ausgewählte Sitzung
     */
    Session getSession(int SessionId);

    /**
     * Holt eine Liste der Sitzungen eines Dozenten vom Server
     *
     * @param owner
     * @return Liste von Sitzungen des angegebenen Besitzers
     */
    List<Session> getOwnedSessions(String owner);

    /**
     * Fügt eine Sitzung hinzu
     *
     * @param name
     * @return true Wenn Erfolgreich false wenn nicht
     */
    boolean addSession(String name);

    /**
     * Löscht eine Sitzung aus der Datenbank
     *
     * @param id
     * @return true wenn Erfolgreich fals wenn nicht
     */
    boolean removeSession(int id);

    /**
     * @param SessionId
     * @return
     */
    String getVotes(int SessionId);

    /**
     * Holt die Antworten einer Sitzung vom Server
     *
     * @param SessionId
     * @return Gibt eine liste von Werten zurück die mit dne 4 Antwortmöglichkeiten korrespondieren
     */
    List<Integer> getAnswers(int SessionId);

    /**
     * @param SessionId
     * @return
     */
    boolean resetAnswers(int SessionId);

    boolean addAnswer(int SessionId);
}
