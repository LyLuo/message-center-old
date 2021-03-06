package cc.ly.mc.core.server.io;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.ly.mc.core.message.GenericMessage;
import static cc.ly.mc.core.message.GenericMessage.HEADER_LENGTH;
import cc.ly.mc.core.message.Message;
import cc.ly.mc.core.message.MessageFactory;

public class SocketServerDecoder extends ByteToMessageDecoder {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SocketServerDecoder.class);

	private static final Integer SKIP_VERSION_CODE_FLAG = 4;

	private static final Integer SKIP_VERSION_CODE_FLAG_LENGTH = 8;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer,
			List<Object> messages) throws Exception {
		if (buffer.readableBytes() < HEADER_LENGTH.get().longValue()) {
			LOGGER.debug( "buffer readableBytes {} is less than {},it is not engouh to decode ",
					buffer.readableBytes(), GenericMessage.HEADER_LENGTH.get());
			return;
		} else {
			buffer.markReaderIndex();
			buffer.skipBytes(SKIP_VERSION_CODE_FLAG);
			long left = buffer.readUnsignedInt() - SKIP_VERSION_CODE_FLAG_LENGTH;
			if (buffer.readableBytes() < left) {
				LOGGER.debug( "buffer readableBytes {} is less than messge's length {},it is not engouh to decode ",
						buffer.readableBytes(), left);
				buffer.resetReaderIndex();
				return;
			}
			buffer.resetReaderIndex();
			Message message = MessageFactory.getInstance().createMessage(buffer);
			messages.add(message);
		}
	}

}
