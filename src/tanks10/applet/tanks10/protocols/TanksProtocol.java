/*
This file is part of Tanks10 Project (http://tanks10.sourceforge.net/).

Tanks10 Project is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Tanks10 Project is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Tanks10 Project.  If not, see <http://www.gnu.org/licenses/>.
*/
package tanks10.protocols;

import java.nio.ByteBuffer;
import java.util.HashMap;

public abstract class TanksProtocol {
	/**
	 * Pola z których korzysta decode/encode,
	 * ten protokół ustawia HashMap<String,String>
	 */
	public final String UNSUPPORTED="unsupported";
	

	public abstract void receive(ByteBuffer in);	// pobiera dane z sieci, obrabia je i wywołuje decode
	public abstract boolean send(ByteBuffer out,Object in);	// umieszcza w buforze binarne dane do wysłania
	protected abstract void decode(String name, HashMap<String,Object> readFields);
	protected abstract void decodeEnd();	// sygnał o końcu czytania grupy wiadomości
	protected abstract String encode(Object in,HashMap<String,Object> sendFields);
	
	// wykonywane gdy cały bufor zostanie zapełniony
	public abstract void reset();
	public abstract void connectionLost();	// wywoływane przy rozłączeniu
	
}
