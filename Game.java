import javax.swing.JOptionPane;

public class Game
{
    // Массивы игрока и компа
    public int masPlay[][];
    public int masComp[][];

    public static int endGame = 3; //0-игра идет 1-Выиграл игрок 2- Выиграл компьютер

    public int P1, P2, P3, P4; // корабли игрока
    public int C1, C2, C3, C4; // корабли компа

    // счетчики ходов
    public int kolHodPlay;
    public int kolHodComp;

    public final int pause = 500;

    // ходы
    public boolean myHod;
    public boolean compHod;

    Thread thread = new Thread();

    Game() 
    {
        masPlay = new int[10][10];
        masComp = new int[10][10];
    }


    public void start() 
    {
        kolHodComp = 0;
        kolHodPlay = 0;

        for (int i = 0; i < 10; i++) 
        {
            for (int j = 0; j < 10; j++) 
            {
                masPlay[i][j] = 0;
                masComp[i][j] = 0;
            }
        }

        myHod = true; //мой ход
        compHod = false;
        endGame = 0; // игра идет
        kolUbitPk(masComp);
        kolUbitPlayer(masPlay);

        if (!Panel.rasstanovka)
        {
            setPalubaPlay();
        }
        setPalubaComp();
    }

    // Атака игрока
    public void attack(int mas[][], int i, int j)
    {
        kolHodPlay++;
        mas[i][j] += 7;
        testUbit(mas, i, j);
        testEndGame();
        thread = new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                //если промах
                if (masComp[i][j] < 8) 
                {
                    myHod = false;
                    compHod = true; //передаем ход компьютеру
                    // Ходит компьютер - пока попадает в цель
                    while (compHod)
                    {
                        try 
                        {
                            Thread.sleep(pause);
                        } catch (InterruptedException e) 
                        {
                            e.printStackTrace();
                        }
                        compHod = compHodit(masPlay);
                    }
                    myHod = true; //передаем ход игроку после промаха компьютера
                }
            }
        });

        thread.start();
    }


     // Проверка конца игры
     // Сумма массива, когда все корабли убиты, равна 15*4+16*2*3+17*3*2+18*4 = 330
    public void testEndGame()
    {
        if (endGame == 0)
        {
        int sumEnd = 330; //когда все корабли убиты
        int sumPlay = 0; // Сумма убитых палуб игрока
        int sumComp = 0; // Сумма убитых палуб компьютера

        kolUbitPk(masComp);
        kolUbitPlayer(masPlay);

            if (endGame == 0) 
            {
                for (int i = 0; i < 10; i++) 
                {
                    for (int j = 0; j < 10; j++) 
                    {
                        // Суммируем подбитые палубы
                        if (masPlay[i][j] >= 15)
                            sumPlay += masPlay[i][j];
                        if (masComp[i][j] >= 15)
                            sumComp += masComp[i][j];
                    }
                }
                if (sumPlay == sumEnd) 
                {
                    endGame = 2;
                    JOptionPane.showMessageDialog(null,
                            "Вы проиграли! Попробуйте еще раз",
                            "Поражение !", JOptionPane.INFORMATION_MESSAGE);

                } else if (sumComp == sumEnd)
                {
                    endGame = 1;
                    //выводим диалоговое окно игроку
                    JOptionPane.showMessageDialog(null,
                            "Поздравляю! Вы выиграли!",
                            "Победа !", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

     // Подсчитываем количество убитых кораблей компьютера
    public void kolUbitPk(int[][]mas)
    {
        P4 = 0;
        P3 = 0;
        P2 = 0;
        P1 = 0;
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                if (mas[i][j] == 18)
                    P4 = (P4 + 1);
                if (mas[i][j] == 17)
                    P3 = (P3 + 1);
                if (mas[i][j] == 16)
                    P2 = (P2 + 1);
                if (mas[i][j] == 15)
                    P1 = (P1 + 1);
            }
        }
        P4 /= 4;
        P3 /= 3;
        P2 /= 2;
    }

     // Считаем убитые корабли игрока
    public void kolUbitPlayer(int[][] mas)
    {
        C4 = 0;
        C3 = 0;
        C2 = 0;
        C1 = 0;
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                if (mas[i][j] == 18)
                    C4 = (C4 + 1);
                if (mas[i][j] == 17)
                    C3 = (C3 + 1);
                if (mas[i][j] == 16)
                    C2 = (C2 + 1);
                if (mas[i][j] == 15)
                    C1 = (C1 + 1);
            }
        }
        C4 /= 4;
        C3 /= 3;
        C2 /= 2;
    }


    private void testUbit(int mas[][], int i, int j)
    {
        if (mas[i][j] == 8) //Если однопалубный
        {
            mas[i][j] += 7; //прибавляем к убитому +7
            setOkrUbit(mas,i,j); //Уменьшаем окружение убитого на 1
        }
        else if (mas[i][j] == 9) //Если двупалубный
        {
            analizUbit(mas,i,j,2);
        }
        else if (mas[i][j] == 10) //Если трехпалубный
        {
            analizUbit(mas,i,j,3);
        }
        else if (mas[i][j] == 11) //Если четырехпалубный
        {
            analizUbit(mas,i,j,4);
        }
    }

    // проверяет корабль
    private void analizUbit(int[][] mas, int i, int j, int kolPalub)
    {
        //Количество раненых палуб
        int kolRanen = 0;
        //Считаем раненые палубы
        for (int x = i-(kolPalub-1); x <= i+(kolPalub-1); x++)
        {
            for (int y = j-(kolPalub-1); y <= j+(kolPalub-1); y++)
            {
                if (testMasOut(x, y) && (mas[x][y] == kolPalub + 7)) // Если это палуба ранена
                    kolRanen++;
            }
        }
        // Если количество раненых палуб совпадает с количеством палуб
        // корабля, то он убит - прибавляем число 7
        if (kolRanen == kolPalub)
        {
            for (int x = i-(kolPalub-1); x <= i+(kolPalub-1); x++)
            {
                for (int y = j-(kolPalub-1); y<= j+(kolPalub-1); y++)
                {
                    if (testMasOut(x, y)&&(mas[x][y]==kolPalub+7))
                    {
                        mas[x][y] += 7; //  палуба убитого корабля
                        setOkrUbit(mas, x, y);
                    }
                }
            }
        }
    }



    // выставление окружения убитого корабля
    public void setOkrKilled(int mas[][],int i,int j)
    {
        if (testMasOut(i, j))
        {
            if (mas[i][j] == -1 || mas[i][j] == 6)
            {
                mas[i][j]--;
            }
        }
    }
    private void setOkrUbit(int[][] mas, int i, int j)
    {
        setOkrKilled(mas, i - 1, j - 1); // сверху слева
        setOkrKilled(mas, i - 1, j); // сверху
        setOkrKilled(mas, i - 1, j + 1); // сверху справа
        setOkrKilled(mas, i, j + 1); // справа
        setOkrKilled(mas, i + 1, j + 1); // снизу справа
        setOkrKilled(mas, i + 1, j); // снизу
        setOkrKilled(mas, i + 1, j - 1); // снизу слева
        setOkrKilled(mas, i, j - 1); // слева
    }

    // ход компа
    boolean compHodit(int mas[][])
    {
        //если идет автоигра или ход компьютера
        if ((endGame == 0 ) || (compHod ))
        {
            kolHodComp++;
            // Признак попадания в цель
            boolean popal = false;
            // Признак выстрела в раненый корабль
            boolean ranen = false;
            //признак направления корабля
            boolean horiz = false;
            _for1:
            // break метка
            // Пробегаем все игровое поле игрока
            for (int i = 0; i < 10; i++)
                for (int j = 0; j < 10; j++)
                    //если находим раненую палубу
                    if ((mas[i][j] >= 9) && (mas[i][j] <= 11))
                    {
                        ranen = true;
                        //ищем подбитую палубу слева и справа
                        if ((testMasOut(i - 3, j) && mas[i - 3][j] >= 9 && (mas[i - 3][j] <= 11))
                                || (testMasOut(i - 2, j) && mas[i - 2][j] >= 9 && (mas[i - 2][j] <= 11))
                                || (testMasOut(i - 1, j) && mas[i - 1][j] >= 9 && (mas[i - 1][j] <= 11))
                                || (testMasOut(i + 3, j) && mas[i + 3][j] >= 9 && (mas[i + 3][j] <= 11))
                                || (testMasOut(i + 2, j) && mas[i + 2][j] >= 9 && (mas[i + 2][j] <= 11))
                                || (testMasOut(i + 1, j) && mas[i + 1][j] >= 9 && (mas[i + 1][j] <= 11)))
                        {
                            horiz = true;
                        }
                        else if ((testMasOut(i, j + 3) && mas[i][j + 3] >= 9 && (mas[i][j + 3] <= 11))
                                //ищем подбитую палубу сверху и снизу
                                || (testMasOut(i, j + 2) && mas[i][j + 2] >= 9 && (mas[i][j + 2] <= 11))
                                || (testMasOut(i, j + 1) && mas[i][j + 1] >= 9 && (mas[i][j + 1] <= 11))
                                || (testMasOut(i, j - 3) && mas[i][j - 3] >= 9 && (mas[i][j - 3] <= 11))
                                || (testMasOut(i, j - 2) && mas[i][j - 2] >= 9 && (mas[i][j - 2] <= 11))
                                || (testMasOut(i, j - 1) && mas[i][j - 1] >= 9 && (mas[i][j - 1] <= 11)))
                        {
                            horiz = false;
                        }
                        //если не удалось определить направление корабля, то выбираем произвольное направление для обстрела
                        else for (int x = 0; x < 50; x++)
                        {
                            int napr = (int) (Math.random() * 4);
                            if (napr == 0 && testMasOut(i - 1, j) && (mas[i - 1][j] <= 4) && (mas[i - 1][j] != -2))
                            {
                                mas[i - 1][j] += 7;
                                //проверяем, убили или нет
                                testUbit(mas, i - 1, j);
                                if (mas[i - 1][j] >= 8) popal = true;
                                //прерываем цикл
                                break _for1;
                            } else if (napr == 1 && testMasOut(i + 1, j) && (mas[i + 1][j] <= 4) && (mas[i + 1][j] != -2))
                            {
                                mas[i + 1][j] += 7;
                                testUbit(mas, i + 1, j);
                                if (mas[i + 1][j] >= 8) popal = true;
                                break _for1;
                            } else if (napr == 2 && testMasOut(i, j - 1) && (mas[i][j - 1] <= 4) && (mas[i][j - 1] != -2))
                            {
                                mas[i][j - 1] += 7;
                                testUbit(mas, i, j - 1);
                                if (mas[i][j - 1] >= 8) popal = true;
                                break _for1;
                            } else if (napr == 3 && testMasOut(i, j + 1) && (mas[i][j + 1] <= 4) && (mas[i][j + 1] != -2))
                            {
                                mas[i][j + 1] += 7;
                                testUbit(mas, i, j + 1);
                                if (mas[i][j + 1] >= 8) popal = true;
                                break _for1;
                            }
                        }
                        //если определили направление, то производим выстрел только в этом напрвлении
                        if (horiz)
                        { //по горизонтали
                            if (testMasOut(i - 1, j) && (mas[i - 1][j] <= 4) && (mas[i - 1][j] != -2))
                            {
                                mas[i - 1][j] += 7;
                                testUbit(mas, i - 1, j);
                                if (mas[i - 1][j] >= 8) popal = true;
                                break _for1;
                            } else if (testMasOut(i + 1, j) && (mas[i + 1][j] <= 4) && (mas[i + 1][j] != -2))
                            {
                                mas[i + 1][j] += 7;
                                testUbit(mas, i + 1, j);
                                if (mas[i + 1][j] >= 8) popal = true;
                                break _for1;
                            }
                        }//по вертикали
                        else if (testMasOut(i, j - 1) && (mas[i][j - 1] <= 4) && (mas[i][j - 1] != -2))
                        {
                            mas[i][j - 1] += 7;
                            testUbit(mas, i, j - 1);
                            if (mas[i][j - 1] >= 8) popal = true;
                            break _for1;
                        } else if (testMasOut(i, j + 1) && (mas[i][j + 1] <= 4) && (mas[i][j + 1] != -2))
                        {
                            mas[i][j + 1] += 7;
                            testUbit(mas, i, j + 1);
                            if (mas[i][j + 1] >= 8) popal = true;
                            break _for1;
                        }
                    }

            // если нет ранненых кораблей
            if (!ranen)
            {
                for (int l = 1; l <= 100; l++)
                {
                    // Находим случайную позицию на игровом поле
                    int i = (int) (Math.random() * 10);
                    int j = (int) (Math.random() * 10);
                    //Проверяем, что можно сделать выстрел
                    if ((mas[i][j] <= 4) && (mas[i][j] != -2))
                    {
                        //делаем выстрел
                        mas[i][j] += 7;
                        //проверяем, что убит
                        testUbit(mas, i, j);
                        if (mas[i][j] >= 8)
                            popal = true;
                        //прерываем цикл
                        break;
                    }
                }
            }
            // проверяем конец игры
            testEndGame();
            // возвращаем результат
            return popal;
        }else
            return false;
    }

     // проверка выхода за пределы массива
    private boolean testMasOut(int i, int j)
    {
        if (((i >= 0) && (i <= 9)) && ((j >= 0) && (j <= 9)))
        {
            return true;
        } else
            return false;
    }

    // для океружения корабля
    private void setOkr(int[][] mas, int i, int j, int val)
    {
        if (testMasOut(i, j) && mas[i][j] == 0)
        {
            mas[i][j] = val;
        }
    }

    // назначение значения в окружение
    private void okrBegin(int[][] mas, int i, int j, int val)
    {
        setOkr(mas, i - 1, j - 1, val);
        setOkr(mas, i - 1, j, val);
        setOkr(mas, i - 1, j + 1, val);
        setOkr(mas, i, j + 1, val);
        setOkr(mas, i, j - 1, val);
        setOkr(mas, i + 1, j + 1, val);
        setOkr(mas, i + 1, j, val);
        setOkr(mas, i + 1, j - 1, val);
    }


    // Ручная установка кораблей
    public boolean setPaluba(int i, int j, int kolPal, boolean napr)
    {
        //признак установки палубы
        boolean flag = false;
        // Если можно расположить палубу
        if (testNewPaluba(masPlay, i, j))
        {
            if (!napr) // вправо
            {
                if (testNewPaluba(masPlay, i, j + (kolPal - 1)))
                    flag = true;
            }
            else if (napr) // вниз
            {
                if (testNewPaluba(masPlay, i + (kolPal - 1), j))
                    flag = true;
            }
        }
        if (flag)
        {
            //Помещаем в ячейку число палуб
            masPlay[i][j] = kolPal;
            // Окружаем минус двойками
            okrBegin(masPlay, i, j, -2);
            if (!napr) // вправо
            {
                for (int k = kolPal - 1; k >= 1; k--)
                {
                    masPlay[i][j + k] = kolPal;
                    okrBegin(masPlay, i, j + k, -2);
                }
            }
            else if (napr) // вниз
            {
                for (int k = kolPal - 1; k >= 1; k--)
                {
                    masPlay[i + k][j] = kolPal;
                    okrBegin(masPlay, i + k, j, -2);
                }
            }
        }
        okrEnd(masPlay); //заменяем -2 на -1
        return flag;
    }



    // можно ли воткнуть палубу
    private boolean testNewPaluba(int [][]mas,int i, int j)
    {
        if (!testMasOut(i, j))
            return false;
        if ((mas[i][j] == 0) || (mas[i][j] == -2))
            return true;
        else
            return false;
    }

    // замена -2 на -1 для заполнения поля
    private void okrEnd(int[][] mas)
    {
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 10; j++)
            {
                if (mas[i][j] == -2)
                    mas[i][j] = -1;
            }
        }
    }


    // рандомная генерация
    private void setPalubaAuto(int [][]mas, int kolPal)
    {
        int i = 0;
        int j = 0;
        while (true)
        {
            boolean flag = false;
            i = (int) (Math.random() * 10);
            j = (int) (Math.random() * 10);
            int napr = (int) (Math.random() * 4); // 0 - вверх. 1 - вправо. 2 - вниз. 3 - влево

            // Если можно расположить палубу
            if (testNewPaluba(mas, i, j))
            {
                if (napr == 0) //вверх
                {
                    if (testNewPaluba(mas, i - (kolPal - 1), j))  //если можно расположить палубу вверх, то flag = true
                        flag = true;
                }
                else if (napr == 1) // вправо
                {
                    if (testNewPaluba(mas, i, j + (kolPal - 1)))
                        flag = true;
                }
                else if (napr == 2) // вниз
                {
                    if (testNewPaluba(mas, i + (kolPal - 1), j))
                        flag = true;
                }
                else if (napr == 3) // влево
                {
                    if (testNewPaluba(mas, i, j - (kolPal - 1)))
                        flag = true;
                }
            }
            if (flag)
            {
                //Помещаем в ячейку число палуб
                mas[i][j] = kolPal;
                // Окружаем минус двойками
                okrBegin(mas, i, j, -2);
                if (napr == 0)  // вверх
                {
                    for (int k = kolPal - 1; k >= 1; k--)
                    {
                        mas[i -k][j] = kolPal;
                        okrBegin(mas, i - k, j, -2);
                    }
                }
                else if (napr == 1) // вправо
                {
                    for (int k = kolPal - 1; k >= 1; k--)
                    {
                        mas[i][j + k] = kolPal;
                        okrBegin(mas, i, j + k, -2);
                    }
                }
                else if (napr == 2) // вниз
                {
                     for (int k = kolPal - 1; k >= 1; k--)
                     {
                         mas[i + k][j] = kolPal;
                         okrBegin(mas, i + k, j, -2);
                     }
                }
                else if (napr == 3) // влево
                {
                    for (int k = kolPal - 1; k >= 1; k--)
                    {
                        mas[i][j -k] = kolPal;
                        okrBegin(mas, i, j - k, -2);
                    }
                }
                //прерываем цикл
                break;
            }
        }
        okrEnd(mas); //заменяем -2 на -1
    }

    // рандомная генерация для игрока
    private void setPalubaPlay()
    {
        setPalubaAuto(masPlay, 4);
        for (int i = 1; i <= 2; i++)
        {
            setPalubaAuto(masPlay, 3);
        }
        for (int i = 1; i <= 3; i++)
        {
            setPalubaAuto(masPlay, 2);
        }
        for (int i = 1; i<= 4;i++)
        {
            setPalubaAuto(masPlay,1);
        }
    }

    // рандомная генерация доля компа
    private void setPalubaComp()
    {
        setPalubaAuto(masComp, 4);
        for (int i = 1; i <= 2; i++)
        {
            setPalubaAuto(masComp, 3);
        }
        for (int i = 1; i <= 3; i++)
        {
            setPalubaAuto(masComp, 2);
        }
        for (int i = 1;i <= 4;i++)
        {
            setPalubaAuto(masComp,1);
        }
    }
}