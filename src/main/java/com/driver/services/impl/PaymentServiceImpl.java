package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        //Attempt a payment of amountSent for reservationId using the given mode ("cASh", "card", or "upi")
        //If the amountSent is less than bill, throw "Insufficient Amount" exception, otherwise update payment attributes
        //If the mode contains a string other than "cash", "card", or "upi" (any character in uppercase or lowercase),
        // throw "Payment mode not detected" exception.
        //Note that the reservationId always exists
        Optional<Reservation> reservationOptional = reservationRepository2.findById(reservationId);
        Reservation reservation = reservationOptional.get();
        int amountDue = reservation.getNumberOfHours() * reservation.getSpot().getPricePerHour();
        if(amountSent < amountDue){
            return null;
        }
        String MODE = mode.toUpperCase();
        String cash = PaymentMode.CASH.name();
        String upi = PaymentMode.UPI.name();
        String card = PaymentMode.CARD.name();
        if(!MODE.equals(cash) && !MODE.equals(upi) && !MODE.equals(card)){
            return null;
        }
        PaymentMode paymentMode = null;
        if(MODE.equals(cash)){
            paymentMode = PaymentMode.CASH;
        } else if(MODE.equals(card)){
            paymentMode = PaymentMode.CARD;
        } else {
            paymentMode = PaymentMode.UPI;
        }
        Payment payment = reservation.getPayment();
        payment.setPaymentCompleted(true);
        payment.setPaymentMode(paymentMode);
        Payment savedPayment = paymentRepository2.save(payment);
        reservationRepository2.save(reservation);
        return  savedPayment;
    }
}
