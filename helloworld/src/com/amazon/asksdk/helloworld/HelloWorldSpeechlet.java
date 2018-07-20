/**
 * Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at
 * <p>
 * http://aws.amazon.com/apache2.0/
 * <p>
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.amazon.asksdk.helloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.SsmlOutputSpeech;

/**
 * This sample shows how to create a simple speechlet for handling speechlet requests.
 */
public class HelloWorldSpeechlet implements SpeechletV2 {
    private static final Logger log = LoggerFactory.getLogger(HelloWorldSpeechlet.class);
    private int calorieCount = 0;
    private String product = null;
    private String pizzaCal = "310";
    private String cookieCal = "80";
    private String quesoCal = "90";
    private Integer score = 0;


    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        log.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        log.info("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        IntentRequest request = requestEnvelope.getRequest();
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                requestEnvelope.getSession().getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        if ("HelloWorldIntent".equals(intentName)) {
            return getHelloResponse();
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        } else if ("WholefoodsHiLoIntent".equals(intentName)) {
            String answer = intent.getSlot("answer").getValue();
            return hiLoResponse(answer);
        } else if ("ReadRulesIntentYes".equals(intentName)) {
            return readRulesResponse();
        } else if ("ProductIntent".equals(intentName)) {
            String product = intent.getSlot("product").getValue();
            return evaluateProduct(product);
        } else if ("ReadRulesIntentNo".equals(intentName)) {
            return beginGameResponse();
        } else if ("RepeatRulesIntentYes".equals(intentName)) {
            return readRulesResponse();
        } else if ("RepeatRulesIntentNo".equals(intentName)) {
            return beginGameResponse();
        } else if ("AMAZON.StopIntent".equals(intentName)) {
            return getHelpResponse();
        } else {
            return getAskResponse("HelloWorld", "This is unsupported.  Please try something else.");
        }
    }

    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        // any cleanup logic goes here
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse hiLoResponse(String answer) {
        // Get the answer that you are getting, from the product
        String speechText = null;
        switch (answer) {
            case "high":
                speechText = evaluatehighResponse(this.product);
                break;
            case "low":
                speechText = evaluatelowResponse(this.product);break;
        }
        return getAskResponse("hi Lo response", speechText);
    }

    private String evaluatehighResponse(String product) {

        String speechText = "";

        if (product.equals("queso")) {
            speechText = "You are right! The actual calorie count is " + this.quesoCal + ". If you want to play again say! lets play!";
            incrementScore();
        } else if (product.equals("pizza")) {
            speechText = "You are wrong! The actual calorie count is " + this.pizzaCal + ". If you want to play again say! lets play!";
        } else if (product.equals("cookie")) {
            speechText = "You are wrong! The actual calorie count is " + this.cookieCal + ". If you want to play again say! lets play!";
        }

        return speechText;
    }

    private void incrementScore() {
        this.score++;
    }

    private String evaluatelowResponse(String product) {

        String speechText = "";

        if (product.equals("queso")) {
            speechText = "You are wrong! The actual calorie count is " + this.quesoCal + ". If you want to play again say! lets play!";
        } else if (product.equals("pizza")) {
            speechText = "You are right! The actual calorie count is " + this.pizzaCal + ". If you want to play again say! lets play!";
            incrementScore();
        } else if (product.equals("cookie")) {
            speechText = "You are right! The actual calorie count is " + this.cookieCal + ". If you want to play again say! lets play!";
            incrementScore();
        }

        return speechText;
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse beginGameResponse() {
        String speechText = "Great, lets get started! Your Options are Cookie, Pizza, Queso !";
        return getAskResponse("begin game", speechText);
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse evaluateProduct(String product) {
        String speechText = null;
        switch (product) {
            case "pizza":
                speechText = "Our vegan pizza is topped with a delicious red sauce, Daiya vegan mozzarella-style shreds and a mix of spinach, tomatoes and Kalamata olives. The Calorie Count is 200. Is this too low or too high?";
                break;
            case "cookie":
                speechText = "The classic decadent Chocolate Cookies we all love! These are perfectly soft, tender and chewy. And they're a lot like a brownie but in cookie form.  In other words these cookies are sure to satisfy! The Calorie Count is 50. Is this too low or too high?";
                break;
            case "queso":
                speechText = "Aloha queso blends creamy Monterey Jack cheese with spicy jalapenos and mild red bell peppers for the perfect medium heat. The Calorie Count is 100. Is this too low or too high?";
                break;
            default:
                speechText = "This option is not supported, try Pizza, Cookie or Queso";
                break;
        }
        return getAskResponse("product Evaluation", speechText);
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse readRulesResponse() {
        String speechText = "In this game, you will select a Whole Foods product.  I will state a calorie value that is around the actual calorie value for that product.  If you think it is lower than actual, please respond with the phrase TOO LOW.  If you think it is higher than actual, please respond with phrase TOO HIGH. Each item that you get correct will earn you a point. Would you like me to repeat the rules? Or Start the game?";
        return getAskResponse("read rules", speechText);
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getWelcomeResponse() {
        String speechText = "Welcome to Whole Foods Hi Lo! Would you like me to read you the rules?";
        return getAskResponse("HelloWorld", speechText);
    }

    /**
     * Creates a {@code SpeechletResponse} for the hello intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelloResponse() {
        String speechText = "Hello world";

        // Create the Simple card content.
        SimpleCard card = getSimpleCard("HelloWorld", speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelpResponse() {
        String speechText = "You can say hello to me!";
        return getAskResponse("HelloWorld", speechText);
    }

    /**
     * Helper method that creates a card object.
     *
     * @param title   title of the card
     * @param content body of the card
     * @return SimpleCard the display card to be sent along with the voice response.
     */
    private SimpleCard getSimpleCard(String title, String content) {
        SimpleCard card = new SimpleCard();
        card.setTitle(title);
        card.setContent(content);

        return card;
    }

    /**
     * Helper method for retrieving an OutputSpeech object when given a string of TTS.
     *
     * @param speechText the text that should be spoken out to the user.
     * @return an instance of SpeechOutput.
     */
    private PlainTextOutputSpeech getPlainTextOutputSpeech(String speechText) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return speech;
    }

    /**
     * Helper method that returns a reprompt object. This is used in Ask responses where you want
     * the user to be able to respond to your speech.
     *
     * @param outputSpeech The OutputSpeech object that will be said once and repeated if necessary.
     * @return Reprompt instance.
     */
    private Reprompt getReprompt(OutputSpeech outputSpeech) {
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(outputSpeech);

        return reprompt;
    }

    /**
     * Helper method for retrieving an Ask response with a simple card and reprompt included.
     *
     * @param cardTitle  Title of the card that you want displayed.
     * @param speechText speech text that will be spoken to the user.
     * @return the resulting card and speech text.
     */
    private SpeechletResponse getAskResponse(String cardTitle, String speechText) {
        SimpleCard card = getSimpleCard(cardTitle, speechText);
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);
        Reprompt reprompt = getReprompt(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    public Integer getScore() {
        return score;
    }
}
