/**
 * Aqui observamos como es que se administran los procesos de la cola de procesos listos y cola FIFO
 * @author leo
 */
package proyecto_so;

public class AdministradorProcesos { //Clase de administrador de procesos
    //Método que simula nuestra carga de procesos, recibiendo los procesos, la cola FIFO, tamaño de memoria y el quantum
    public void simularCargaProcesos(ColaProcesosListos procesos, ColaFIFO colaFIFO, int tamanoMemoria,int quantum, int totProcesos) {
        //Inicializando variables   
        int memoriaDisponible = tamanoMemoria; //Memoria disponible depende de lo que pida el usuario  
        int Quantum = quantum; //El quantum depende del usuario
        int tiempo = procesos.getInicio().proceso.getTiempoLlegada(); // Obtenemos el tiempo de llegada del primer proceso
        Proceso enEjecucion = null; //enProceso en ejecición empieza como nulo
        int bandera=0;  //Inicializamos bandera en 0
        int banderaEspacio=0; //Espacio de bandera inicia en 0 para controlar la asignación de memoria a los procesos.
        int tiempototal=procesos.sumarTiemposServicio(); //Nuestro tiempo total es dependiendo de la suma dada en la cola de procesos listos
        
        int esperaPromedio = 0;
        int respuestaPromedio = 0;
        int ejecucionPromedio = 0; 
        //int tiempoAnterior = 0;
        
        System.out.println("\n<----->  Tiempo total de servicio de todos los procesos = "+tiempototal + " <----->");
        while ((!procesos.estaVacia() || !colaFIFO.estaVacia())|| tiempo!=tiempototal) { //Mientras haya procesos en la cola de procesos listos, haya procesos en la cola FIFO o el tiempo de la simulación no haya alcanzado el tiempo total de servicio de todos los procesos.
            // Bucle mientras existan procesos en la cola de procesos
            while(procesos.estaVacia()==false && procesos.getInicio().proceso.getTiempoLlegada()<=tiempo && banderaEspacio==0){ //Mientras la cola de procesos no esté vacia y el tiempo de llegada del primer proceso sea menor o igual al tiempo actual de la simulación y la bandera de espacio sea 0
                Proceso proceso = procesos.getInicio().getProceso(); //Obtenemos el primer proceso de la cola de procesos listos
                if (proceso.getTamano() <= memoriaDisponible) { //Si el tamaño del proceso es menor o igual al de la memoria disponible
                    procesos.eliminarProceso(); //Se puede eliminar de la cola de procesos debido a que existe suficiente espacio
                    colaFIFO.encolar(proceso, quantum); //Se sube a la cola FIFO
                     
                    memoriaDisponible -= proceso.getTamano(); //Restamos el tamaño de memoria que ocupa el proceso y esa es nuestra nueva memoria disponible
                    System.out.println("\n-> El proceso " + proceso.getId() + " subio a la cola de Procesos listos para ejecucion y restan " + memoriaDisponible + " unidades de memoria en el tiempo "+tiempo);    //Imprimimos datos            
                    colaFIFO.imprimirContenido(); //Imprimimos contenido de nuestra cola FIFO
                } 
                else { //En otro caso, donde no hay suficeinte memoria
                    System.out.println("-> El proceso " + proceso.getId() + " no puede ser cargado en memoria, por lo tanto va a la cola de procesos listos"); //Imprimimos que no se puede
                    //procesos.devolver(proceso);
                    banderaEspacio=1; //Indicamos que no hay espacio para cargar más memoria
                    procesos.imprimirContenido(); //Imprimimos el contenido de la cola de procesos listos
                    colaFIFO.imprimirContenido(); //Imprimimos el contenido de nuetsra cola FIFO
                }     
            }
         
            if (bandera==0 || enEjecucion==null){ //Si nuestra bandera está en 0 ó en ejecución sigue nulo
                System.out.println("--- Tiempo: " +tiempo +"  |  Memoria: "+tamanoMemoria+ "   ---"); //Imprimimos tiempo que llevamos y memoria
                enEjecucion=colaFIFO.desencolar(); //Desencolamos para que sea el siguiente en ejecutarse en la CPU
                
                if (enEjecucion==null) {//Si no hay nada ejecutandose
                    System.out.println("Esperando proceso ...."); //Estamos esperando proceso
                }else{//En otro caso
                    System.out.println("El proceso "+enEjecucion.getId()+" esta en ejecucion en el tiempo "+tiempo);} //Mostramos que proceso y en que tiempo se ejecuta
                //tiempoAnterior = tiempo;
                if(enEjecucion.getBanderaTiempos() == 0){
                        enEjecucion.setEsperaMin(tiempo-enEjecucion.getTiempoLlegada());
                        enEjecucion.setBanderaTiempos(enEjecucion.getBanderaTiempos()+1);
                        respuestaPromedio += enEjecucion.getEsperaMin();
                }
                else if(enEjecucion.getBanderaTiempos() > 0){
                        enEjecucion.setEsperaMax(tiempo);
                    }
                //enEjecucion.setBanderaTiempos(enEjecucion.getBanderaTiempos()+1);
                bandera=1; //Indicamos que ya tenemos un proceso ejecutandose
            }
           
            if (Quantum==0) { //Si nuestro quantum es 0 (Quantum agotado)
                System.out.println("\n---| Tiempo: " +tiempo +" |---"); //Imprimimos tiempo actual
                Quantum = quantum; //Reiniciamos quantum
                if (enEjecucion.getTiempoServicio()==0) { //Si el tiempo de servicio del proceso en ejecución es 0 (terminó su ejecución)
                    memoriaDisponible += enEjecucion.getTamano(); //Sumammos el tamaño del proceso en ejecución a nuestra memoria disponible, liberando espacio
                    //enEjecucion.setEjecucionMax(tiempo);
//                    int tmp = enEjecucion.getEjecutado();
//                    int t_ejecutado = tmp + (tiempo-tiempoAnterior);
//                    enEjecucion.setEjecutado(t_ejecutado);
//                    System.out.println(enEjecucion.getId()+" ejecuto: "+enEjecucion.getEjecutado());
                    System.out.println("El proceso "+ enEjecucion.getId()+ " ha concluido en el tiempo "+tiempo +" liberando "+enEjecucion.getTamano() + " unidades de memoria :D"); //Imprimimos datos
                    if(enEjecucion.getTemp() == 0){
                        enEjecucion.setEjecucionMax(tiempo-enEjecucion.getTiempoLlegada());
                        enEjecucion.setTemp(enEjecucion.getTemp()+1);
                        //System.out.println("Primer caso: "+enEjecucion.getId() + " --> "+enEjecucion.getEjecucionMax());
                        ejecucionPromedio += enEjecucion.getEjecucionMax();
                    }
                    System.out.println("Memoria disponible actual = "+memoriaDisponible); //Imprimimos memoria disponible actual
                    banderaEspacio=0; //Indicamos que hay suficiente espacio para cargar más procesos
                } else{ //Si el tiempo de servicio del proceso en ejecución no ha terminado su ejecución 
                    colaFIFO.encolar(enEjecucion, quantum); //Lo volvemos a enconlar a nuestra cola FIFO
                }
                enEjecucion=colaFIFO.desencolar(); //Se desencola el siguiente proceso de la cola FIFO para ejecutarlo en la CPU.
                
                if(enEjecucion==null){ //Si no hay nada ejecutandose
                        System.out.println("\nEsperando Proceso...."); //Marcamos que estamos esperando proceso
                    }else{
                    System.out.println("El proceso " + enEjecucion.getId() + " subio en el tiempo "+tiempo + " a la CPU");} //Indicamos cuando subió a CPU
                    //tiempoAnterior = tiempo;
                    if(enEjecucion.getBanderaTiempos() == 0){
                        enEjecucion.setEsperaMin(tiempo-enEjecucion.getTiempoLlegada());
                        enEjecucion.setBanderaTiempos(enEjecucion.getBanderaTiempos()+1);
                        respuestaPromedio += enEjecucion.getEsperaMin();
                    } else if(enEjecucion.getBanderaTiempos() > 0){
                        enEjecucion.setEsperaMax(tiempo);
                    }
            }else{
                
                // Cuando el Quantum no se termina, pero el de rafaga ya se acabo
                 if (enEjecucion.getTiempoServicio()==0) { // Si el tiempo de servicio de nuetsro proceso en ejecución es 0
                    banderaEspacio=0; //Tenemos espacio disponible
                    //enEjecucion.setTiempoEjecucion(tiempo - enEjecucion.getTiempoLlegada()); //
                    System.out.println("\n--- Tiempo: " +tiempo +" ---");
                    Quantum=quantum; // Se reinicia el quantum
                    memoriaDisponible += enEjecucion.getTamano(); // Se libera la memoria ocupada por el proceso que ha completado su ejecución.
//                    int tmp = enEjecucion.getEjecutado();
//                    int t_ejecutado = tmp + (tiempo-tiempoAnterior);
//                    enEjecucion.setEjecutado(t_ejecutado);
//                    System.out.println(enEjecucion.getId()+" ejecuto: "+enEjecucion.getEjecutado());
                    System.out.println("El proceso "+ enEjecucion.getId()+ " ha concluido en el tiempo "+tiempo +" liberando "+enEjecucion.getTamano()+" unidades de memoria :D"); //Imprimimos                 
                    if(enEjecucion.getTemp() == 0){
                        enEjecucion.setEjecucionMax(tiempo-enEjecucion.getTiempoLlegada());
                        enEjecucion.setTemp(enEjecucion.getTemp()+1);
                        //System.out.println("Segundo caso :"+ enEjecucion.getId() + " --> " +enEjecucion.getEjecucionMax());
                        ejecucionPromedio += enEjecucion.getEjecucionMax();
                    }
                    
                    enEjecucion=colaFIFO.desencolar(); //Se desencola el siguiente proceso de la cola FIFO para ejecutarlo en la CPU.
                    
                    if(enEjecucion==null){//Si no hay nada ejecutandose
                        System.out.println("Esperando Proceso...."); //Imprimimos 
                    }else{
                    System.out.println("El proceso " + enEjecucion.getId() + " subio en el tiempo "+tiempo + " a la CPU");}//Indicamos cuando subió a CPU
                    //System.out.println(enEjecucion.getContador());
                    if(enEjecucion.getBanderaTiempos() == 0){
                        enEjecucion.setEsperaMin(tiempo-enEjecucion.getTiempoLlegada());
                        enEjecucion.setBanderaTiempos(enEjecucion.getBanderaTiempos()+1);
                        respuestaPromedio += enEjecucion.getEsperaMin();
                    }
                    else if(enEjecucion.getBanderaTiempos() > 0){
                        enEjecucion.setEsperaMax(tiempo);
                    }

                 }
            }
            
        if(enEjecucion==null){ //Si la ejecución está vacía 
            System.out.println("Esperando Proceso...."); //Imprimimos
        }else{
        Quantum--; //Restamos quatum
        tiempo++; //Restamos tiempo actual
            
        enEjecucion.setTiempoEjecucion(enEjecucion.getTiempoServicio()-1);
        //System.out.println(enEjecucion.getId()+": "+enEjecucion.getEsperaMax());
        //System.out.println("Espera max de "+ enEjecucion.getId()+ ": "+enEjecucion.getEsperaMax());
        //System.out.println("Ejecucion max de "+ enEjecucion.getId()+ ": "+enEjecucion.getEjecucionMax());
        enEjecucion.setEjecutado((enEjecucion.getContador() - 1) * quantum);
        enEjecucion.setTiempoEspera(enEjecucion.getEsperaMax() - enEjecucion.getTiempoLlegada() - enEjecucion.getEjecutado());
        System.out.println(enEjecucion.getId() + " Tiempo de espera: " + enEjecucion.getTiempoEspera());

        // Verificar si el tiempo de espera ya se ha sumado
        if (enEjecucion.getBanderaEspera() != 1) {
            System.out.println("Sumando tiempo de espera para " + enEjecucion.getId() + ": " + enEjecucion.getTiempoEspera());

            // Sumar el tiempo de espera a esperaPromedio
            esperaPromedio += enEjecucion.getTiempoEspera();

            // Establecer la bandera para indicar que el tiempo de espera se ha sumado
            enEjecucion.setBanderaEspera(1);
        } else {
            System.out.println("El tiempo de espera para " + enEjecucion.getId() + " ya se ha sumado.");
        }

        // Después del bucle, restablecer la bandera de espera para el siguiente ciclo
        enEjecucion.setBanderaEspera(0);


        
        } //Actualizamos el tiempo de ejecucion del proceso actual, restando 1 al tiempo de servicio restante del proceso, estbalecinedo el nuevo valor como el tiempo de ejecucion del proceso
        
        }
        //SOLUCION TEMPORAL: AL CALCULAR EL TIEMPO DE EJECUCION, NO SE MARCA CUANDO TERMINA LA EJECUCION DEL ULTIMO PROCESO, POR LO QUE 
        //HAY UN ERROR DE CALCULO. CUANDO SE SOLUCIONE ESO, BORRAR LA SIGUIENTE LINEA, 
        ejecucionPromedio += tiempo;
        
        System.out.println("|| Memoria " +memoriaDisponible + " ||"); //Imprimimos memoria disponible
        System.out.println("|| Tiempo "+tiempo + " ||"); //Imprimimos tiempo
        
        System.out.println("\n\nTiempo de espera promedio = " + (float)esperaPromedio/(float)totProcesos);
        System.out.println("\nTiempo de respuesta promedio = " + (float)respuestaPromedio/(float)totProcesos);
        System.out.println("\nTiempo de ejecucion promedio = " + (float)ejecucionPromedio/(float)totProcesos + "\n\n");

        

        procesos.imprimirContenido(); //Imprimimos contenido de la cola de procesos listos
        colaFIFO.imprimirContenido(); //Imprimimos contenido de la cola FIFO
    } 
}