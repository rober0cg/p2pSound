package sr.GetOpts;

/**************************************************************************
/* LongOpt.java -- Long option object for Getopt
/**************************************************************************/

import java.text.MessageFormat;

/**************************************************************************/
public class LongOpt extends Object {

/**************************************************************************/
/* Class Variables */
public static final int NO_ARGUMENT = 0;
public static final int REQUIRED_ARGUMENT = 1;
public static final int OPTIONAL_ARGUMENT = 2;

/**************************************************************************/
/* Instance Variables */
protected String name;
protected int has_arg;
protected StringBuffer flag;
protected int val;

/* Mensajes de error */
private int iLongOpt_invalidValue = 0;
private String[] sLongOpt_messages = {
  "Valor no válido {0} para el parámetro 'has_arg'"
};

/**************************************************************************/
/* Constructors */

/**
  * Create a new LongOpt object with the given parameter values.  If the value passed as has_arg is not valid, then an exception is thrown.
  * @param name The long option String.
  * @param has_arg Indicates whether the option has no argument (NO_ARGUMENT), a required argument (REQUIRED_ARGUMENT) or an optional argument (OPTIONAL_ARGUMENT).
  * @param flag If non-null, this is a location to store the value of "val" when this option is encountered, otherwise "val" is treated as the equivalent short option character.
  * @param val The value to return for this long option, or the equivalent single letter option to emulate if flag is null.
  * @exception IllegalArgumentException If the has_arg param is not one of NO_ARGUMENT, REQUIRED_ARGUMENT or OPTIONAL_ARGUMENT.
  */
public LongOpt(String name, int has_arg, StringBuffer flag, int val) throws IllegalArgumentException {
  // Validate has_arg
  if ((has_arg != NO_ARGUMENT) && (has_arg != REQUIRED_ARGUMENT) && (has_arg != OPTIONAL_ARGUMENT)) {
      Object[] msgArgs = { new Integer(has_arg).toString() };
      throw new IllegalArgumentException(MessageFormat.format(sLongOpt_messages[iLongOpt_invalidValue], msgArgs));
    }

  // Store off values
  this.name = name;
  this.has_arg = has_arg;
  this.flag = flag;
  this.val = val;
}

/**************************************************************************/
/**
  * Returns the name of this LongOpt as a String
  * @return Then name of the long option
  */
public String getName() {
  return(name);
}

/**************************************************************************/
/**
  * Returns the value set for the 'has_arg' field for this long option
  * @return The value of 'has_arg'
  */
public int getHasArg() {
  return(has_arg);
}

/**************************************************************************/
/** 
  * Returns the value of the 'flag' field for this long option
  * @return The value of 'flag'
  */
public StringBuffer getFlag() {
  return(flag);
}

/**
  *  Returns the value of the 'val' field for this long option
  * @return The value of 'val'
  */
public int getVal() {
  return(val);
}

/**************************************************************************/

} // Class LongOpt
