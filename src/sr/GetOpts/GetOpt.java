package sr.GetOpts;

/**************************************************************************
/* GetOpt.java -- Java port of GNU getopt from glibc 2.0.6
/**************************************************************************/

import java.text.MessageFormat;

public class GetOpt extends Object {

/**************************************************************************/
/* Class Variables */
protected static final int REQUIRE_ORDER = 1;
protected static final int PERMUTE = 2;
protected static final int RETURN_IN_ORDER = 3;

/**************************************************************************/
/* Instance Variables */
protected String optarg;
protected int optind = 0;
protected boolean opterr = true;
protected int optopt = '?';
protected String nextchar;
protected String optstring;
protected LongOpt[] long_options;
protected boolean long_only;
protected int longind;
protected boolean posixly_correct;
protected boolean longopt_handled;
protected int first_nonopt = 1;
protected int last_nonopt = 1;
private boolean endparse = false;
protected String[] argv;
protected int ordering;
protected String progname;

/* Mensajes de error */
private int iGetOpt_ambigious = 0;
private int iGetOpt_arguments1 = 1;
private int iGetOpt_arguments2 = 2;
private int iGetOpt_requires= 3;
private int iGetOpt_unrecognized = 4;
private int iGetOpt_unrecognized2 = 5;
private int iGetOpt_illegal = 6;
private int iGetOpt_invalid = 7;
private int iGetOpt_requires2 = 8;
private String[] sGetOpt_messages = {
    "{0}: la opción ''{1}'' es ambigua",
    "{0}: la opción ''--{1}'' no permite un argumento",
    "{0}: la opción ''{1}{2}'' no permite un argumento",
    "{0}: la opción ''{1}'' requiere un argumento",
    "{0}: opción no reconocida ''--{1}''",
    "{0}: opción no reconocida ''{1}{2}''",
    "{0}: opción ilegal -- {1}",
    "{0}: opción no válida -- {1}",
    "{0}: la opción requiere un argumento -- {1}"
};

/**************************************************************************/
/* Constructors */

/** Construct a basic GetOpt instance with the given input data.  Note that this handles "short" options only.
  * @param progname The name to display as the program name when printing errors
  * @param argv The String array passed as the command line to the program.
  * @param optstring A String containing a description of the valid args for this program
  */
public GetOpt(String progname, String[] argv, String optstring) {
  this(progname, argv, optstring, null, false);
}

/** Construct a GetOpt instance with given input data that is capable of parsing long options as well as short.
  * @param progname The name to display as the program name when printing errors
  * @param argv The String array passed as the command ilne to the program
  * @param optstring A String containing a description of the valid short args for this program
  * @param long_options An array of LongOpt objects that describes the valid long args for this program
  */
public GetOpt(String progname, String[] argv, String optstring, LongOpt[] long_options) {
  this(progname, argv, optstring, long_options, false);
}

/** Construct a GetOpt instance with given input data that is capable of parsing long options and short options.  Contrary to what you might
  * think, the flag 'long_only' does not determine whether or not we scan for only long arguments.  Instead, a value of true here allows
  * long arguments to start with a '-' instead of '--' unless there is a conflict with a short option name.
  * @param progname The name to display as the program name when printing errors
  * @param argv The String array passed as the command ilne to the program
  * @param optstring A String containing a description of the valid short args for this program
  * @param long_options An array of LongOpt objects that describes the valid long args for this program
  * @param long_only true if long options that do not conflict with short options can start with a '-' as well as '--'
  */
public GetOpt(String progname, String[] argv, String optstring, LongOpt[] long_options, boolean long_only) {
    if (optstring.length() == 0) {
      optstring = " ";
    }
  // This function is essentially _getopt_initialize from GNU getopt
    this.progname = progname;
    this.argv = argv;
    this.optstring = optstring;
    this.long_options = long_options;
    this.long_only = long_only;

  // Determine how to handle the ordering of options and non-options
    if (optstring.charAt(0) == '-') {
        ordering = RETURN_IN_ORDER;
        if (optstring.length() > 1)
            this.optstring = optstring.substring(1);
    }
    else if (optstring.charAt(0) == '+') {
        ordering = REQUIRE_ORDER;
        if (optstring.length() > 1)
            this.optstring = optstring.substring(1);
    }
    else if (posixly_correct) {
        ordering = REQUIRE_ORDER;
    }
    else {
        ordering = PERMUTE; // The normal default case
    }
}

/**************************************************************************/
/* Instance Methods */

/**************************************************************************/
/**
  *  In GNU getopt, it is possible to change the string containg valid options on the fly because it is passed as an argument to getopt() each time.  In
  * this version we do not pass the string on every call.  In order to allow dynamic option string changing, this method is provided.
  * @param optstring The new option string to use
  */
public void setOptstring(String optstring) {
    if (optstring.length() == 0) {
        optstring = " ";
    }
    this.optstring = optstring;
}

/**************************************************************************/
/**
  * optind it the index in ARGV of the next element to be scanned. This is used for communication to and from the caller
  * and for communication between successive calls to `getopt'.
  * When `getopt' returns -1, this is the index of the first of the non-option elements that the caller should itself scan.
  * Otherwise, `optind' communicates from one call to the next how much of ARGV has been scanned so far.  
  */
public int getOptind() {
    return(optind);
}

/**************************************************************************/
/**
  * This method allows the optind index to be set manually.  Normally this is not necessary (and incorrect usage of this method can lead to serious
  * lossage), but optind is a public symbol in GNU getopt, so this method  was added to allow it to be modified by the caller if desired.
  * @param optind The new value of optind
  */
public void setOptind(int optind) {
    this.optind = optind;
}

/**************************************************************************/
/**
  * Since in GNU getopt() the argument vector is passed back in to the function every time, the caller can swap out argv on the fly.  Since
  * passing argv is not required in the Java version, this method allows the user to override argv.  Note that incorrect use of this method can
  * lead to serious lossage.
  * @param argv New argument list
  */
public void setArgv(String[] argv) {
    this.argv = argv;
}

/**************************************************************************/
/** 
  * For communication from `getopt' to the caller.
  * When `getopt' finds an option that takes an argument, the argument value is returned here.
  * Also, when `ordering' is RETURN_IN_ORDER, each non-option ARGV-element is returned here.
  * No set method is provided because setting this variable has no effect.
  */
public String getOptarg() {
    return(optarg);
}

/**************************************************************************/
/**
  * Normally GetOpt will print a message to the standard error when an invalid option is encountered.  This can be suppressed (or re-enabled)
  * by calling this method.  There is no get method for this variable  because if you can't remember the state you set this to, why should I?
  */
public void setOpterr(boolean opterr) {
    this.opterr = opterr;
}

/**************************************************************************/
/**
  * When getopt() encounters an invalid option, it stores the value of that option in optopt which can be retrieved with this method.
  * There is no corresponding set method because setting this variable has no effect.
  */
public int getOptopt() {
    return(optopt);
}

/**************************************************************************/
/**
  * Returns the index into the array of long options (NOT argv) representing the long option that was found.
  */
public int getLongind() {
    return(longind);
}

/**************************************************************************/
/**
  * Exchange the shorter segment with the far end of the longer segment. That puts the shorter segment into the right place.
  * It leaves the longer segment in the right place overall, but it consists of two parts that need to be swapped next.
  * This method is used by getopt() for argument permutation.
  */
protected void exchange(String[] argv) {
    int bottom = first_nonopt;
    int middle = last_nonopt;
    int top = optind;
    String tem;

    while (top > middle && middle > bottom) {
        if (top - middle > middle - bottom) {
          // Bottom segment is the short one. 
            int len = middle - bottom;
            int i;

          // Swap it with the top part of the top segment. 
            for (i = 0; i < len; i++) {
                tem = argv[bottom + i];
                argv[bottom + i] = argv[top - (middle - bottom) + i];
                argv[top - (middle - bottom) + i] = tem;
            }
          // Exclude the moved bottom segment from further swapping. 
            top -= len;
        }
        else {
          // Top segment is the short one.
            int len = top - middle;
            int i;

          // Swap it with the bottom part of the bottom segment. 
            for (i = 0; i < len; i++) {
                tem = argv[bottom + i];
                argv[bottom + i] = argv[middle + i];
                argv[middle + i] = tem;
            }
          // Exclude the moved top segment from further swapping. 
            bottom += len;
        }
    }

  // Update records for the slots the non-options now occupy. 

    first_nonopt += (optind - last_nonopt);
    last_nonopt = optind;
}

/**************************************************************************/
/**
  * Check to see if an option is a valid long option.  Called by getopt().
  * Put in a separate method because this needs to be done twice.  (The C getopt authors just copy-pasted the code!).
  * @param longind A buffer in which to store the 'val' field of found LongOpt
  * @return Various things depending on circumstances
  */
protected int checkLongOption() {
  LongOpt pfound = null;
  int nameend;
  boolean ambig;
  boolean exact;
  
  longopt_handled = true;
  ambig = false;
  exact = false;
  longind = -1;

  nameend = nextchar.indexOf("=");
  if (nameend == -1)
    nameend = nextchar.length();
  
  // Test all lnog options for either exact match or abbreviated matches
  for (int i = 0; i < long_options.length; i++)
    {
      if (long_options[i].getName().startsWith(nextchar.substring(0, nameend)))
        {
          if (long_options[i].getName().equals(nextchar.substring(0, nameend)))
            {
              // Exact match found
              pfound = long_options[i];
              longind = i;
              exact = true;
              break;
            }
          else if (pfound == null)
            {
              // First nonexact match found
              pfound = long_options[i];
              longind = i;
            }
          else
            {
              // Second or later nonexact match found
              ambig = true;
            }
        }
    } // for
  
  // Print out an error if the option specified was ambiguous
  if (ambig && !exact)
    {
      if (opterr)
        {
          Object[] msgArgs = { progname, argv[optind] };
          System.err.println(MessageFormat.format(sGetOpt_messages[iGetOpt_ambigious], msgArgs));
        }

       nextchar = "";
       optopt = 0;
       ++optind;
 
       return('?');
    }
 
  if (pfound != null)
    {
      ++optind;
 
      if (nameend != nextchar.length())
        {
          if (pfound.has_arg != LongOpt.NO_ARGUMENT)
            {
              if (nextchar.substring(nameend).length() > 1)
                optarg = nextchar.substring(nameend+1);
              else
                optarg = "";
            }
          else
            {
              if (opterr)
                {
                  // -- option
                  if (argv[optind - 1].startsWith("--"))
                    {
                      Object[] msgArgs = { progname, pfound.name };
                      System.err.println(MessageFormat.format(sGetOpt_messages[iGetOpt_arguments1],msgArgs));
                    }
                  // +option or -option
                  else
                    {
                      Object[] msgArgs = { progname, new 
                               Character(argv[optind-1].charAt(0)).toString(),
                               pfound.name };
                      System.err.println(MessageFormat.format(sGetOpt_messages[iGetOpt_arguments2],msgArgs));
                    }
                 }
   
              nextchar = "";
              optopt = pfound.val;
   
              return('?');
            }
        } // if (nameend)
      else if (pfound.has_arg == LongOpt.REQUIRED_ARGUMENT)
        {
          if (optind < argv.length)
            {
               optarg = argv[optind];
               ++optind;
            }
          else
            {
              if (opterr)
                {
                  Object[] msgArgs = { progname, argv[optind-1] };
                  System.err.println(MessageFormat.format(sGetOpt_messages[iGetOpt_requires],msgArgs));
                }
   
              nextchar = "";
              optopt = pfound.val;
              if (optstring.charAt(0) == ':')
                return(':');
              else
                return('?');
            }
        } // else if (pfound)
   
      nextchar = "";

      if (pfound.flag != null)
        {
          pfound.flag.setLength(0);
          pfound.flag.append(pfound.val);
   
          return(0);
        }

      return(pfound.val);
   } // if (pfound != null)
  
  longopt_handled = false;

  return(0);
}

/**************************************************************************/
/**
  * This method returns a char that is the current option that has been parsed from the command line.  If the option takes an argument, then
  * the internal variable 'optarg' is set which is a String representing the the value of the argument.  This value can be retrieved by the
  * caller using the getOptarg() method.  If an invalid option is found, an error message is printed and a '?' is returned.  The name of the
  * invalid option character can be retrieved by calling the getOptopt() method.  When there are no more options to be scanned, this method
  * returns -1.  The index of first non-option element in argv can be retrieved with the getOptind() method.
  * @return Various things as described above
  */
public int getopt() {
  optarg = null;

  if (endparse == true)
    return(-1);

  if ((nextchar == null) || (nextchar.equals("")))
    {
      // If we have just processed some options following some non-options,
      //  exchange them so that the options come first.
      if (last_nonopt > optind)
        last_nonopt = optind;
      if (first_nonopt > optind)
        first_nonopt = optind;

      if (ordering == PERMUTE)
        {
          // If we have just processed some options following some non-options,
          // exchange them so that the options come first.
          if ((first_nonopt != last_nonopt) && (last_nonopt != optind))
            exchange(argv);
          else if (last_nonopt != optind)
            first_nonopt = optind;

          // Skip any additional non-options
          // and extend the range of non-options previously skipped.
          while ((optind < argv.length) && (argv[optind].equals("") ||
            (argv[optind].charAt(0) != '-') || argv[optind].equals("-")))
            {
              optind++;
            }
          
          last_nonopt = optind;
        }

      // The special ARGV-element `--' means premature end of options.
      // Skip it like a null option,
      // then exchange with previous non-options as if it were an option,
      // then skip everything else like a non-option.
      if ((optind != argv.length) && argv[optind].equals("--"))
        {
          optind++;

          if ((first_nonopt != last_nonopt) && (last_nonopt != optind))
            exchange (argv);
          else if (first_nonopt == last_nonopt)
            first_nonopt = optind;

          last_nonopt = argv.length;

          optind = argv.length;
        }

      // If we have done all the ARGV-elements, stop the scan
      // and back over any non-options that we skipped and permuted.
      if (optind == argv.length)
        {
          // Set the next-arg-index to point at the non-options
          // that we previously skipped, so the caller will digest them.
          if (first_nonopt != last_nonopt)
            optind = first_nonopt;

          return(-1);
        }

      // If we have come to a non-option and did not permute it,
      // either stop the scan or describe it to the caller and pass it by.
      if (argv[optind].equals("") || (argv[optind].charAt(0) != '-') || 
          argv[optind].equals("-"))
        {
          if (ordering == REQUIRE_ORDER)
            return(-1);

            optarg = argv[optind++];
            return(1);
        }
      
      // We have found another option-ARGV-element.
      // Skip the initial punctuation.
      if (argv[optind].startsWith("--"))
        nextchar = argv[optind].substring(2);
      else
        nextchar = argv[optind].substring(1);
   }

  // Decode the current option-ARGV-element.

  /* Check whether the ARGV-element is a long option.

     If long_only and the ARGV-element has the form "-f", where f is
     a valid short option, don't consider it an abbreviated form of
     a long option that starts with f.  Otherwise there would be no
     way to give the -f short option.

     On the other hand, if there's a long option "fubar" and
     the ARGV-element is "-fu", do consider that an abbreviation of
     the long option, just like "--fu", and not "-f" with arg "u".

     This distinction seems to be the most useful approach.  */
  if ((long_options != null) && (argv[optind].startsWith("--")
      || (long_only && ((argv[optind].length()  > 2) || 
      (optstring.indexOf(argv[optind].charAt(1)) == -1)))))
    {
       int c = checkLongOption();

       if (longopt_handled)
         return(c);
         
      // Can't find it as a long option.  If this is not getopt_long_only,
      // or the option starts with '--' or is not a valid short
      // option, then it's an error.
      // Otherwise interpret it as a short option.
      if (!long_only || argv[optind].startsWith("--")
        || (optstring.indexOf(nextchar.charAt(0)) == -1))
        {
          if (opterr)
            {
              if (argv[optind].startsWith("--"))
                {
                  Object[] msgArgs = { progname, nextchar };
                  System.err.println(MessageFormat.format(sGetOpt_messages[iGetOpt_unrecognized],msgArgs));
                }
              else
                {
                  Object[] msgArgs = { progname, new 
                                 Character(argv[optind].charAt(0)).toString(), 
                                 nextchar };
                  System.err.println(MessageFormat.format(sGetOpt_messages[iGetOpt_unrecognized2],msgArgs));
                }
            }

          nextchar = "";
          ++optind;
          optopt = 0;
    
          return('?');
        }
    } // if (longopts)

  // Look at and handle the next short option-character */
  int c = nextchar.charAt(0); //**** Do we need to check for empty str?
  if (nextchar.length() > 1)
    nextchar = nextchar.substring(1);
  else
    nextchar = "";
  
  String temp = null;
  if (optstring.indexOf(c) != -1)
    temp = optstring.substring(optstring.indexOf(c));

  if (nextchar.equals(""))
    ++optind;

  if ((temp == null) || (c == ':'))
    {
      if (opterr)
        {
          if (posixly_correct)
            {
              // 1003.2 specifies the format of this message
              Object[] msgArgs = { progname, new 
                                   Character((char)c).toString() };
              System.err.println(MessageFormat.format(sGetOpt_messages[iGetOpt_illegal], msgArgs));
            }
          else
            {
              Object[] msgArgs = { progname, new 
                                   Character((char)c).toString() };
              System.err.println(MessageFormat.format(sGetOpt_messages[iGetOpt_invalid], msgArgs));
            }
        }

      optopt = c;

      return('?');
    }

  // Convenience. Treat POSIX -W foo same as long option --foo
  if ((temp.charAt(0) == 'W') && (temp.length() > 1) && (temp.charAt(1) == ';'))
    {
      if (!nextchar.equals(""))
        {
          optarg = nextchar;
        }
      // No further cars in this argv element and no more argv elements
      else if (optind == argv.length)
        {
          if (opterr)
            {
              // 1003.2 specifies the format of this message. 
              Object[] msgArgs = { progname, new 
                                   Character((char)c).toString() };
              System.err.println(MessageFormat.format(sGetOpt_messages[iGetOpt_requires2], msgArgs));
            }

          optopt = c;
          if (optstring.charAt(0) == ':')
            return(':');
          else
            return('?');
        }
      else
        {
          // We already incremented `optind' once;
          // increment it again when taking next ARGV-elt as argument. 
          nextchar = argv[optind];
          optarg  = argv[optind];
        }

      c = checkLongOption();

      if (longopt_handled)
        return(c);
      else
        // Let the application handle it
        {
          nextchar = null;
          ++optind;
          return('W');
        }
    }

  if ((temp.length() > 1) && (temp.charAt(1) == ':'))
    {
      if ((temp.length() > 2) && (temp.charAt(2) == ':'))
        // This is an option that accepts and argument optionally
        {
          if (!nextchar.equals(""))
            {
               optarg = nextchar;
               ++optind;
            }
          else
            {
              optarg = null;
            }

          nextchar = null;
        }
      else
        {
          if (!nextchar.equals(""))
            {
              optarg = nextchar;
              ++optind;
            }
          else if (optind == argv.length)
            {
              if (opterr)
                {
                  // 1003.2 specifies the format of this message
                  Object[] msgArgs = { progname, new 
                                       Character((char)c).toString() };
                  System.err.println(MessageFormat.format(sGetOpt_messages[iGetOpt_requires2], msgArgs));
                }

              optopt = c;
 
              if (optstring.charAt(0) == ':')
                return(':');
              else
                return('?');
            }
          else
            {
              optarg = argv[optind];
              ++optind;

              // Ok, here's an obscure Posix case.  If we have o:, and
              // we get -o -- foo, then we're supposed to skip the --,
              // end parsing of options, and make foo an operand to -o.
              // Only do this in Posix mode.
              if ((posixly_correct) && optarg.equals("--"))
                {
                  // If end of argv, error out
                  if (optind == argv.length)
                    {
                      if (opterr)
                        {
                          // 1003.2 specifies the format of this message
                          Object[] msgArgs = { progname, new 
                                               Character((char)c).toString() };
                          System.err.println(MessageFormat.format(sGetOpt_messages[iGetOpt_requires2],msgArgs));
                        }

                      optopt = c;
 
                      if (optstring.charAt(0) == ':')
                        return(':');
                      else
                        return('?');
                    }

                  // Set new optarg and set to end
                  // Don't permute as we do on -- up above since we
                  // know we aren't in permute mode because of Posix.
                  optarg = argv[optind];
                  ++optind;
                  first_nonopt = optind;
                  last_nonopt = argv.length;
                  endparse = true;
                }
            }

          nextchar = null;
        }
    }

  return(c);
}

} // Class GetOpt

