package mx.unison;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VendorCSVFile {
    private String fileName;

    public VendorCSVFile(String fileName) {
        this.fileName = fileName;
    }

    public void write(Vendor v) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(fileName, true), true);
            out.println(v.toString());
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(VendorCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Vendor find(int codigo) {
        String lookFor = String.valueOf(codigo);
        String record = null;
        Vendor x = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            while ((record = in.readLine()) != null) {
                if (record.startsWith(lookFor)) {
                    x = parseRecord(record);
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VendorCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VendorCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        return x;
    }

    public void modify(int codigo, String newNombre, Date newFecha, String newZona) {
        String lookFor = String.valueOf(codigo);
        String record = null;
        StringBuilder updatedContent = new StringBuilder();

        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            while ((record = in.readLine()) != null) {
                if (record.startsWith(lookFor)) {
                    Vendor existingVendor = parseRecord(record);
                    existingVendor.setNombre(newNombre);
                    existingVendor.setFecha(newFecha);
                    existingVendor.setZona(newZona);
                    updatedContent.append(existingVendor.toString()).append("\n");
                } else {
                    updatedContent.append(record).append("\n");
                }
            }
            in.close();

            // Write the updated content back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(updatedContent.toString());
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VendorCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VendorCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Date parseDOB(String d) throws ParseException {
        int len = d.length();
        Date date = null;
        if (len == 8) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
            date = dateFormat.parse(d);
        }
        if (len == 10) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            date = dateFormat.parse(d);
        }
        return date;
    }

    private Vendor parseRecord(String record) {
        StringTokenizer st1 = new StringTokenizer(record, ",");

        Vendor v = new Vendor();

        v.setCodigo(Integer.parseInt(st1.nextToken()));
        v.setNombre(st1.nextToken());
        String fecha = st1.nextToken();

        Date dob = null;
        try {
            dob = parseDOB(fecha);
        } catch (ParseException e) {
            System.out.printf(e.getMessage());
        }
        v.setFecha(dob);
        v.setZona(st1.nextToken());
        return v;
    }

    public void agregarNuevoVendedor() {
        Scanner input = new Scanner(System.in);

        System.out.println("Agregar un nuevo vendedor:");

        System.out.print("Número de empleado: ");
        int codigoEmpleado = input.nextInt();
        input.nextLine(); // Consume la nueva línea en el búfer

        System.out.print("Nombre: ");
        String nombre = input.nextLine();

        System.out.print("Fecha de ingreso (MM/dd/yyyy): ");
        String fechaIngreso = input.nextLine();

        System.out.print("Zona: ");
        String zona = input.nextLine();

        // Pide las ventas mensuales
        System.out.print("Ventas mensuales: ");
        double ventasMensuales = input.nextDouble();

        // Crea un nuevo objeto Vendor con los datos ingresados
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date fecha = dateFormat.parse(fechaIngreso);
            Vendor nuevoVendedor = new Vendor(codigoEmpleado, nombre, fecha, zona, ventasMensuales);

            // Escribe el nuevo vendedor en el archivo
            write(nuevoVendedor);

            System.out.println("Nuevo vendedor agregado exitosamente.");
        } catch (ParseException e) {
            System.out.println("Error al analizar la fecha de ingreso.");
        }
    }
    public void delete(int codigoDelete) {
        String lookFor = String.valueOf(codigoDelete);
        String record = null;
        StringBuilder updatedContent = new StringBuilder();

        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            while ((record = in.readLine()) != null) {
                if (record.startsWith(lookFor)) {
                    continue; // Omite la línea para eliminar el registro
                }
                updatedContent.append(record).append("\n");
            }
            in.close();

            // Escribe el contenido actualizado de nuevo en el archivo
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(updatedContent.toString());
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VendorCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VendorCSVFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static void main(String[] args) {

        final String fileName = "C:\\Users\\Uziel\\OneDrive\\Escritorio\\random_files\\vendors.csv";
        //final String fileName = "D:\\data\\vendors-data.csv";

        VendorCSVFile csvFile = new VendorCSVFile(fileName);

        Scanner input = new Scanner(System.in);

        // Opción para agregar nuevo vendedor
        System.out.println("¿Desea agregar un nuevo vendor? (SI/NO)");
        String respuesta2 = input.next();
        if (respuesta2.equalsIgnoreCase("SI")) {
            csvFile.agregarNuevoVendedor();
        }else System.out.println("No se agrego ningun vendor");

        System.out.print("\n Numero de empleado: ");


        int codigoEmpleado = input.nextInt();
        long t1 = System.currentTimeMillis();
        Vendor p = csvFile.find(codigoEmpleado);
        long t2 = System.currentTimeMillis();
        System.out.println(p);
        System.out.println(t2 - t1);

        // Opcion para borrar un vendedor
        System.out.println("¿Desea borrar los datos del empleado? (SI/NO)");
        String respuestaBorrar = input.next();
        if (respuestaBorrar.equalsIgnoreCase("SI")) {
            csvFile.delete(codigoEmpleado);
            System.out.println("Datos del empleado borrados con éxito.");
        }


        // Opción para modificar datos
        System.out.println("¿Desea modificar los datos del empleado? (SI/NO)");
        String respuesta = input.next();
        if (respuesta.equalsIgnoreCase("SI")) {
            input.nextLine(); // Limpiar el buffer
            System.out.println("Nuevo nombre:");
            String newNombre = input.nextLine();
            System.out.println("Nueva fecha (MM/dd/yyyy):");
            String newFechaStr = input.next();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date newFecha = null;
            try {
                newFecha = dateFormat.parse(newFechaStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println("Nueva zona:");
            String newZona = input.next();
            csvFile.modify(codigoEmpleado, newNombre, newFecha, newZona);
            System.out.println("Datos modificados con éxito.");
        }


    }
}
