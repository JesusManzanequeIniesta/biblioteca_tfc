/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jmi.bibliotecatfc.exceptions;

/**
 *
 * @author yisus
 */
@SuppressWarnings("serial")
public class BibliotecaException extends Exception {

    int codigoError;

    public BibliotecaException() {

    }

    public BibliotecaException(String message, int codigoError) {
        super(message);
        this.codigoError = codigoError;
    }

    public int getErrorCode() {
        return codigoError;
    }
}
