package org.dice_research.squirrel.analyzer.mime;

import org.apache.jena.riot.RDFLanguages;

import java.util.ArrayList;

/**
 * Building different finite state machines.
 *
 * @author Abhishek Hassan Chandrashekar (abhihc@mail.uni-paderborn.de).
 */
public class FiniteStateMachineFactory {

    static public FiniteStateMachine create(String mimeType) {
        return new FiniteStateMachineFactory().buildStateMachine(mimeType);
    }

    private FiniteStateMachine buildStateMachine(String mimeType) {
        switch (mimeType) {
            case "RDF/XML":
                return buildRDFXMLStateMachine();
            case "Turtle":
                return buildTurtleStateMachine();
            case "N-Triples":
                return buildNTriplesStateMachine();
            case "RDF/JSON":
                return buildRDFJSONStateMachine();
            case "JSON-LD":
                return buildJSONLDStateMachine();
            default:
                return null;
        }
    }

    /**
     * Builds a finite state machine to validate a simple
     * RDFXML file
     */
    private FiniteStateMachine buildRDFXMLStateMachine() {

        ArrayList<State> listOfStates = new ArrayList<>();

        String[] validRules = {"<", "\\?", "x", "m", "l"};
        String[] invalidRules = {"[^<]", "[^\\?]", "[^x]", "[^m]", "[^l]"};

        populateStates(listOfStates, validRules.length);
        populateTransitions(listOfStates, validRules, invalidRules);

        return new Automata(listOfStates.get(0), RDFLanguages.RDFXML);
    }

    /**
     * Builds a finite state machine to validate a simple
     * NTRIPLES file
     */

    private FiniteStateMachine buildNTriplesStateMachine() {

        ArrayList<State> listOfStates = new ArrayList<>();

        String[] validRules = {"<", "h", "t", "t", "p"};
        String[] invalidRules = {"[^<]", "[^h]", "[^t]", "[^t]", "[^p]"};

        populateStates(listOfStates, validRules.length);
        populateTransitions(listOfStates, validRules, invalidRules);

        return new Automata(listOfStates.get(0), RDFLanguages.NTRIPLES);
    }

    /**
     * Builds a finite state machine to validate a simple
     * TURTLE file
     */
    private FiniteStateMachine buildTurtleStateMachine() {


        ArrayList<State> listOfStates = new ArrayList<>();

        String[] validRules = {"\\@", "p", "r", "e", "f", "i", "x"};
        String[] invalidRules = {"[^\\@]", "[^p]", "[^r]", "[^e]", "[^f]", "[^i]", "[^x]"};

        populateStates(listOfStates, validRules.length);
        populateTransitions(listOfStates, validRules, invalidRules);

        return new Automata(listOfStates.get(0), RDFLanguages.TURTLE);
    }


    /**
     * Builds a finite state machine to validate a simple
     * RDFJSON file
     */

    private FiniteStateMachine buildRDFJSONStateMachine() {

        ArrayList<State> listOfStates = new ArrayList<>();

        String[] validRules = {"\\{", "\"", "h", "t", "t", "p"};
        String[] invalidRules = {"[^\\{]", "[^\"]", "[^h]", "[^t]", "[^t]", "[^p]"};

        populateStates(listOfStates, validRules.length);
        populateTransitions(listOfStates, validRules, invalidRules);

        return new Automata(listOfStates.get(0), RDFLanguages.RDFJSON);

    }

    /**
     * Builds a finite state machine to validate a simple
     * JSONLD file
     */

    private FiniteStateMachine buildJSONLDStateMachine() {

        ArrayList<State> listOfStates = new ArrayList<>();

        String[] validRules = {"\\{", "\"", "\\@"};
        String[] invalidRules = {"[^\\{]", "[^\"]", "[^\\@]"};

        populateStates(listOfStates, validRules.length);
        populateTransitions(listOfStates, validRules, invalidRules);

        return new Automata(listOfStates.get(0), RDFLanguages.JSONLD);

    }

    private void populateStates(ArrayList<State> current, int numberOfStates) {
        for (int i = 0; i < numberOfStates; i++)
            current.add(new RtState());
        current.add(new RtState(true, false));
        current.add(new RtState(true, true));
    }

    private void populateTransitions(ArrayList<State> current, String[] validRules, String[] invalidRules) {

        int i = 0;

        for (int j = 0; j < (current.size() - 2); j++) {
            current.get(j).with(new RtTransition(validRules[i], current.get(j + 1)));
            current.get(j).with(new RtTransition(invalidRules[i++], current.get(current.size() - 1)));
        }
    }


}
