package org.example;
import java.net.*;

public class DNSLookup {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java DNSLookup <domain>");
            return;
        }

        String domain = args[0];

        try {
            InetAddress dnsServer = InetAddress.getByName("8.8.8.8");
            int dnsPort = 53;

            // Собираем DNS запрос
            byte[] requestData = buildDNSQuery(domain);

            // Создаем сокет для отправки запроса
            DatagramSocket socket = new DatagramSocket();

            // Отправляем запрос на DNS сервер
            DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, dnsServer, dnsPort);
            socket.send(requestPacket);

            // Получаем ответ
            byte[] responseData = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length);
            socket.receive(responsePacket);

            // Разбираем ответ и выводим IP адрес
            String ipAddress = parseDNSResponse(responsePacket.getData());
            System.out.println("IP address for " + domain + ": " + ipAddress);

            // Закрываем сокет
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Собирает DNS запрос для получения записи A-типа
    private static byte[] buildDNSQuery(String domain) {
        // Заголовок DNS запроса
        byte[] header = new byte[]{
                0, 0, // ID
                1, 0, // Flags: standard query
                0, 1, // Questions count
                0, 0, // Answers count
                0, 0, // Authorities count
                0, 0  // Additional count
        };

        // Парсим доменное имя
        String[] domainParts = domain.split("\\.");
        byte[][] domainBytes = new byte[domainParts.length][];
        int totalLength = 0;
        for (int i = 0; i < domainParts.length; i++) {
            domainBytes[i] = domainParts[i].getBytes();
            totalLength += domainBytes[i].length + 1;
        }

        // Записываем доменное имя в байтовый массив
        byte[] dnsQuestion = new byte[totalLength + 6]; // 6 = 2 bytes for each of QTYPE and QCLASS
        int offset = 0;
        for (int i = 0; i < domainParts.length; i++) {
            dnsQuestion[offset++] = (byte) domainBytes[i].length;
            System.arraycopy(domainBytes[i], 0, dnsQuestion, offset, domainBytes[i].length);
            offset += domainBytes[i].length;
        }
        dnsQuestion[offset++] = 0; // End of domain name
        dnsQuestion[offset++] = 0; // QTYPE (high byte)
        dnsQuestion[offset++] = 1; // QTYPE (low byte) - A record type
        dnsQuestion[offset++] = 0; // QCLASS (high byte)
        dnsQuestion[offset++] = 1; // QCLASS (low byte) - IN class

        // Соединяем заголовок и вопрос в один запрос
        byte[] requestData = new byte[header.length + dnsQuestion.length];
        System.arraycopy(header, 0, requestData, 0, header.length);
        System.arraycopy(dnsQuestion, 0, requestData, header.length, dnsQuestion.length);

        return requestData;
    }

    // Разбирает DNS ответ и извлекает IP адрес
    private static String parseDNSResponse(byte[] responseData) {
        // Пропускаем заголовок DNS ответа
        int offset = 12;
        // Пропускаем доменное имя
        while (responseData[offset] != 0) {
            offset += responseData[offset] + 1;
        }
        offset += 5; // Дополнительные 5 байт для пропуска QTYPE и QCLASS

        // звлекаем IP адрес из ответа
        StringBuilder ipAddress = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (ipAddress.length() > 0) {
                ipAddress.append(".");
            }
            ipAddress.append(responseData[offset++] & 0xFF);
        }
        return ipAddress.toString();
    }
}
