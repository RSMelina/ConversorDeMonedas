import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConversorDeMonedas {

    static class ExchangeRateResponse {
        JsonObject conversion_rates;
    }

    public static void main(String[] args) {
        Map<String, String> monedas = new HashMap<>();
        monedas.put("1","USD");
        monedas.put("2","MXN");
        monedas.put("3","CAD");
        monedas.put("4","GBP");
        monedas.put("5","EUR");
        monedas.put("6","JPY");
        monedas.put("7","AUD");
        monedas.put("8","CNY");

        Scanner lectura= new Scanner(System.in);
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        while (true) {
            System.out.println(""" 
                    
                    Ingrese el número de la moneda base de esta lista:
                    1 USD - Dolar americano
                    2 MXN - Peso mexicano
                    3 CAD - Dolar canadiense
                    4 GBP - Libra esterlina
                    5 EUR - Euro
                    6 JPY - Yen japonés
                    7 AUD - Dolar Australiano
                    8 CNY - Yuan chino
                    9 Salir
                    """);
            String monedaBase = lectura.nextLine();
            String siglasMonedaBase = "";
            if (monedas.containsKey(monedaBase)) {
                siglasMonedaBase = monedas.get(monedaBase);
                System.out.println("La moneda base es: " + siglasMonedaBase + "\n");
            } else {
                System.out.println("No se encontró la moneda base: " +monedaBase);
            }

            if(monedaBase.equalsIgnoreCase("9")){
                break;
            }


            String apiKey = "dfe4246f1554fa13eba8d709";
            String baseURL = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + siglasMonedaBase;

            try{
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseURL))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String json = response.body();

                // Convertir la respuesta JSON a un objeto Java
                ConversorDeMonedas.ExchangeRateResponse exchangeRateResponse = gson.fromJson(json, ConversorDeMonedas.ExchangeRateResponse.class);

                // Obtener las tasas de cambio
                JsonObject rates = exchangeRateResponse.conversion_rates;
                //System.out.println(rates);

                //Cantidad a convertir
                Scanner scannerCantidad = new Scanner(System.in);
                System.out.println("Ingrese la cantidad que deseas convertir: ");
                double cantidadAConvertir = Double.parseDouble(scannerCantidad.nextLine());

                //Moneda a convertir
                Scanner scanner = new Scanner(System.in);
                System.out.println("\n"+ "Del menú de monedas anterior, ingrese el número de la moneda a la que desea convertir: ");
                String monedaAConvertir = scanner.nextLine();
                String siglasMonedaAConvertir = "";
                if (monedas.containsKey(monedaAConvertir)) {
                    siglasMonedaAConvertir = monedas.get(monedaAConvertir);
                    System.out.println("La moneda a convertir es: " + siglasMonedaAConvertir + "\n");
                } else {
                    System.out.println("No se encontró la moneda a convertir: " +monedaAConvertir);
                }

                //Conversiòn de moneda
                double total = 0;
                if (rates.has(siglasMonedaAConvertir)) {
                    double rate = rates.get(siglasMonedaAConvertir).getAsDouble();
                    total = rate * cantidadAConvertir;
                } else {
                    System.out.println("No se encontró la tasa de cambio para la moneda " + monedaAConvertir);
                }

                // Mostrar resultados
                System.out.println("El total de " + cantidadAConvertir + " " + siglasMonedaBase.toUpperCase() + " a " + siglasMonedaAConvertir + " es: " + total + "\n");
                //System.out.println(gson.toJson(rates))
            } catch(NumberFormatException e){
                System.out.println("Ingrese un número válido.");
            } catch (IOException  | InterruptedException e ) {
                throw new RuntimeException(e);
            }
        }
    }
}
