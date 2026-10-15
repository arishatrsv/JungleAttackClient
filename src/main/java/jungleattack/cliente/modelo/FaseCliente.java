package jungleattack.cliente.modelo;

//Fases por las que pasa la aplicación del cliente. Cada fase tiene su pantalla.
public enum FaseCliente {

    INICIO, // Pantalla de inicio. escribir el apodo y crear o unirse a una partida.
    ESPERA,// Sala de espera. ya hay partida, se espera al otro jugador.
    SELECCION,//de especie
    JUEGO,
    RESUMEN
}
