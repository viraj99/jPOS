/*
 * Copyright (c) 2000 jPOS.org.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the jPOS project 
 *    (http://www.jpos.org/)". Alternately, this acknowledgment may 
 *    appear in the software itself, if and wherever such third-party 
 *    acknowledgments normally appear.
 *
 * 4. The names "jPOS" and "jPOS.org" must not be used to endorse 
 *    or promote products derived from this software without prior 
 *    written permission. For written permission, please contact 
 *    license@jpos.org.
 *
 * 5. Products derived from this software may not be called "jPOS",
 *    nor may "jPOS" appear in their name, without prior written
 *    permission of the jPOS project.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  
 * IN NO EVENT SHALL THE JPOS PROJECT OR ITS CONTRIBUTORS BE LIABLE FOR 
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS 
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the jPOS Project.  For more
 * information please see <http://www.jpos.org/>.
 */

package org.jpos.iso;
import java.io.IOException;
import java.io.InputStream;

/**
 * Similar to Europay format, but instead of LLTT it's TTLL
 * <code>
 * Format TTLL....
 * Where 
 *       TT is the 2 digit field number (Tag)
 *       LL is the 2 digit field length
 *       .. is the field content   
 * </code>
 *
 * @author Alejandro Revilla
 * @version $Id$
 * @see IFEP_LLCHAR
 */
public class IFMC_LLCHAR extends ISOFieldPackager {
    public IFMC_LLCHAR() {
	super();
    }
    /**
     * @param len - field len
     * @param description symbolic descrption
     */
    public IFMC_LLCHAR (int len, String description) {
        super(len, description);
    }
    /**
     * @param c - a component
     * @return packed component
     * @exception ISOException
     */
    public byte[] pack (ISOComponent c) throws ISOException {
        int len;
        String s = (String) c.getValue();
    
        if ((len=s.length()) > getLength() || len>97)   // paranoia settings
            throw new ISOException (
                "invalid len "+len +" packing LLMC_CHAR field "
		+(Integer) c.getKey() + " maxlen=" + getLength()
            );

        return (
	    ISOUtil.zeropad(((Integer) c.getKey()).toString(), 2) 
	   +ISOUtil.zeropad(Integer.toString(len), 2) 
	   +s
	).getBytes();
    }

    /**
     * @param c - the Component to unpack
     * @param b - binary image
     * @param offset - starting offset within the binary image
     * @return consumed bytes
     * @exception ISOException
     */
    public int unpack (ISOComponent c, byte[] b, int offset)
        throws ISOException
    {
	if (!(c instanceof ISOField))
	    throw new ISOException 
		(c.getClass().getName() + " is not an ISOField");
        int len = Integer.parseInt(new String(b, offset+2, 2));
	((ISOField)c).setFieldNumber (
	    Integer.parseInt(new String(b, offset, 2))
	);
        c.setValue (new String (b, offset+4, len));
        return len + 4;
    }
    public void unpack (ISOComponent c, InputStream in) 
        throws IOException, ISOException
    {

	if (!(c instanceof ISOField))
	    throw new ISOException 
		(c.getClass().getName() + " is not an ISOField");

        int fldno = Integer.parseInt(new String(readBytes (in, 2)));
        int len   = Integer.parseInt(new String(readBytes (in, 2)));
	((ISOField)c).setFieldNumber (fldno);
        c.setValue (new String (readBytes (in, len)));
    }

    public int getMaxPackedLength() {
        return getLength() + 4;
    }
}
